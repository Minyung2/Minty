package com.Reboot.Minty.job.controller;

import com.Reboot.Minty.job.dto.JobDto;
import com.Reboot.Minty.job.dto.JobSearchDto;
import com.Reboot.Minty.job.entity.Job;
import com.Reboot.Minty.job.service.JobService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobList/**")
    public String jobList(){return "../static/index";}

    @GetMapping(value = { "/api/jobList/","/api/jobList/{page}", "/api/jobList/searchQuery/{searchQuery}/{page}" })
    @ResponseBody
    public Map<String, Object> getJobList(
            JobSearchDto jobSearchDto,
            @PathVariable(value = "searchQuery", required = false) Optional<String> searchQuery,
            @PathVariable(value = "page", required = false) Optional<Integer> page
    ) {
        jobSearchDto.setSearchBy("content"); // or other field
        jobSearchDto.setSearchQuery(searchQuery.orElse(null));
        Pageable pageable = PageRequest.of(page.orElse(0), 10, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Job> jobPage = jobService.getJobPage(jobSearchDto, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("jobPage", jobPage);
        response.put("totalPages", jobPage.getTotalPages());
        response.put("page", jobPage.getNumber() + 1); // Add 1 to match the frontend page numbering

        return response;
    }

    @PostMapping("/jobWrite")
    @ResponseBody
    public ResponseEntity<?> jobSave(@Valid JobDto jobDto,
                                     BindingResult bindingResult,
                                     @RequestPart("fileUpload") List<MultipartFile> mf
                                     , HttpServletRequest request){
        Map<String, String> errors = new HashMap<>();
        HttpSession session = request.getSession();
        if(bindingResult.hasErrors()){
            errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        }

        Long jobId;
        Long userId = (Long) session.getAttribute("userId");
        try{
            jobId = jobService.save(userId, jobDto, mf);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        return ResponseEntity.ok(jobId);
    }
}
