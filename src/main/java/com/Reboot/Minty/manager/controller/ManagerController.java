package com.Reboot.Minty.manager.controller;

import com.Reboot.Minty.manager.dto.ManagerStatisticsDto;
import com.Reboot.Minty.manager.entity.Admin;
import com.Reboot.Minty.manager.repository.AdminRepository;
import com.Reboot.Minty.manager.service.ManagerStatisticsService;
import com.Reboot.Minty.support.entity.Ad;
import com.Reboot.Minty.support.repository.AdRepository;
import com.Reboot.Minty.support.service.AdService;
import io.jsonwebtoken.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ManagerController {
    private final ManagerStatisticsService managerStatisticsService;

    private final AdService adService;
    private final AdRepository adRepository;
    private final AdminRepository adminRepository;

    public ManagerController(ManagerStatisticsService managerStatisticsService, AdService adService, AdRepository adRepository, AdminRepository adminRepository) {
        this.managerStatisticsService = managerStatisticsService;
        this.adService = adService;
        this.adRepository = adRepository;
        this.adminRepository = adminRepository;
    }

    @GetMapping(value = "manager")
    public String manager(Model model) {
        List<ManagerStatisticsDto> statisticsList = managerStatisticsService.getAllManagerStatistics();

        model.addAttribute("statisticsList", statisticsList);
        return "manager/dashboard";
    }



    @GetMapping("/adminPage")
    public String adminPage(Model model){
        List<Ad> ads = adService.getAllAds();
        model.addAttribute("ads", ads);
        return "ad/adminPage";
    }

    private void sendEmail(String recipientEmail, String subject, String content) {
        // 이메일 세션 생성
        Session session = null;
        try {
            // SMTP 서버 설정
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.naver.com");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.ssl.enable", "true"); // SSL 보안 연결 설정


            // 발신자 이메일 계정 정보
            String senderEmail = "k_hyojin82@naver.com";
            String senderPassword = "kbsc3982#";

            session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            // 이메일 메시지 생성
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(content);

            // 이메일 발송
            Transport.send(message);

            System.out.println("이메일이 성공적으로 발송되었습니다.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("이메일 발송 중 오류가 발생했습니다.");
        } finally {
            if (session != null) {
                try {
                    session.getTransport().close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PostMapping("/admin/ads/{id}/approve")
    public String approveAd(@PathVariable("id") Long id) {
        adService.updateAdStatus(id, "승인");
        saveAdToAdmin(id);

        // 이메일 발송
        try {
            Optional<Ad> adOptional = adRepository.findById(id);
            if (adOptional.isPresent()) {
                Ad ad = adOptional.get();
                String recipientEmail = ad.getEmail();
                if (recipientEmail != null) {
                    String subject = "광고 승인 안내";
                    String content = ad.getAdvertiserName() + "님이 요청하신 광고가 승인되었습니다.";
                    sendEmail(recipientEmail, subject, content);
                } else {
                    System.out.println("광고에 이메일 주소가 없습니다.");
                }
            } else {
                System.out.println("광고를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("이메일 발송 중 오류가 발생했습니다.");
        }

        return "redirect:/adminPage";
    }


    @PostMapping("/admin/ads/{id}/reject")
    public String rejectAd(@PathVariable("id") Long id) {
        adService.updateAdStatus(id, "거부");
        saveAdToAdmin(id);

        // 이메일 발송
        Optional<Ad> adOptional = adRepository.findById(id);
        if (adOptional.isPresent()) {
            Ad ad = adOptional.get();
            String recipientEmail = ad.getEmail();
            if (recipientEmail != null) {
                String subject = "광고 거절 안내";
                String content = ad.getAdvertiserName() + "님이 요청하신 광고가 거절되었습니다.";
                sendEmail(recipientEmail, subject, content);
            } else {
                System.out.println("광고에 이메일 주소가 없습니다.");
            }
        } else {
            System.out.println("광고를 찾을 수 없습니다.");
        }

        return "redirect:/adminPage";
    }


    // Admin 엔티티에 저장하는 메소드 추가
    private void saveAdToAdmin(Long adId) {
        try {
            Ad ad = adRepository.findById(adId).orElse(null);

            if (ad != null && ad.getStatus().equals("승인")) {
                Admin admin = new Admin();
                admin.setAdvertiserName(ad.getAdvertiserName());
                admin.setAmount(ad.getAmount());
                admin.setEmail(ad.getEmail());
                admin.setEndDate(ad.getEndDate());
                admin.setStartDate(ad.getStartDate());
                admin.setImage(ad.getImage());
                admin.setStatus(ad.getStatus());

                ad.setAdmin(admin); // Ad 엔티티와 Admin 엔티티 간의 관계를 설정

                adminRepository.save(admin);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외 정보를 콘솔에 출력
        }
    }



    @GetMapping("/adAdmin/{id}")
    public String adAdminPage(@PathVariable("id") Long id, Model model) {
        Optional<Ad> adOptional = adService.getAdById(id);
        if (adOptional.isPresent()) {
            Ad ad = adOptional.get();
            model.addAttribute("ad", ad);
            return "ad/adAdmin";
        } else {
            return "ad/adminPage";
        }
    }


    @GetMapping("/mainPage")
    public String mainPage(Model model) {
        List<Ad> approvedAds = adRepository.findByStatus("승인");

        List<Ad> approvedImages = new ArrayList<>();

        for (Ad ad : approvedAds) {
            String imagePath = "adimage/" + ad.getImage();
            Resource resource = new ClassPathResource("static/" + imagePath);

            try {
                if (resource.exists()) {
                    approvedImages.add(ad);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!approvedImages.isEmpty()) {
            int randomIndex = (int) (Math.random() * approvedImages.size());
            Ad ad = approvedImages.get(randomIndex);
            model.addAttribute("ad", ad);

            String imagePath = "adimage/";
            model.addAttribute("imagePath", imagePath);
        }

        return "ad/mainPage";
    }

}
