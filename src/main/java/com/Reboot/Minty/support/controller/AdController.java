package com.Reboot.Minty.support.controller;

import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.support.entity.Ad;
import com.Reboot.Minty.support.repository.AdRepository;
import com.Reboot.Minty.support.service.AdService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class AdController {
    private final AdService adService;
    private final UserService userService;
    public AdController(AdService adService, UserService userService){
        this.adService = adService;
        this.userService = userService;
    }

    @Autowired
    private AdRepository adRepository;


    @GetMapping("/adBoard")
    public String showAdBoard(Model model) {
        List<Ad> ads = adRepository.findAll();

        model.addAttribute("ads", ads);

        return "ad/adBoard";
    }


    @GetMapping("/adWrite")
    public String AdWriteForm(Model model){
        model.addAttribute("ad", new Ad());
        return "ad/adWrite";
    }

    @PostMapping("/adWrite")
    public String submitAdForm(@ModelAttribute Ad ad, @RequestParam("imageFile") MultipartFile imageFile, HttpSession session) {
        if (!imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();
                String filePath = "D:/intellijPrac/Minty00/src/main/resources/static/adimage/"; // Replace with the actual file path where you want to save the images
                String savedFileName = filePath + fileName;
                imageFile.transferTo(new File(savedFileName));

                ad.setImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LocalDate currentDate = LocalDate.now();
        ad.setRequestDate(currentDate);
        ad.setEndDate(ad.getStartDate().plusDays(ad.getDuration()));

        if (ad.getDuration() == 7) {
            ad.setAmount(500000);
        } else if (ad.getDuration() == 30) {
            ad.setAmount(2000000);
        } else if (ad.getDuration() == 90) {
            ad.setAmount(5000000);
        } else if (ad.getDuration() == 180) {
            ad.setAmount(8000000);
        } else if (ad.getDuration() == 365) {
            ad.setAmount(15000000);
        }

        adService.saveAd(ad);

        return "redirect:/adBoard";
    }

}
