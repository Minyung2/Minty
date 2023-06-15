package com.Reboot.Minty.member.service;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.entity.UserLocation;
import com.Reboot.Minty.member.repository.UserLocationRepository;
import com.Reboot.Minty.member.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserLocationRepository userLocationRepository;

    public UserService(UserRepository userRepository, UserLocationRepository userLocationRepository) {
        this.userRepository = userRepository;
        this.userLocationRepository = userLocationRepository;
    }

    // 회원 가입 DB 저장
    public User saveUser(User user) {
        validateDuplicateMember(user);
        validateDuplicateNickName(user);
        return userRepository.save(user);
    }

    // User의 PK 조회
    public Long getUserId(String email) {
        User user = userRepository.findByEmail(email);
        return user.getId();
    }

    public User getUserInfoById(Long id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return user;
    }

    public User getUserInfo(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

    // 중복 회원 확인
    private void validateDuplicateMember(User user) {
        User findUser = userRepository.findByEmail(user.getEmail());
        if (findUser != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }


    // 위치 인증 저장
    @Transactional
    public void saveUserLocation(User user, String latitude, String longitude, String address) {
        UserLocation userLocation = new UserLocation();
        userLocation.setLatitude(latitude);
        userLocation.setLongitude(longitude);
        userLocation.setAddress(address);
        userLocation.setUser(user);
        System.out.println(userLocation.getUser());
        System.out.println(userLocation.getAddress());
        System.out.println(userLocation.getLatitude());
        System.out.println(userLocation);
        userLocationRepository.save(userLocation);
    }

    // DB에 해당 회원이 위치 정보 값 있는지 판단
    public boolean userHasLocation(Long userId) {
        long count = userLocationRepository.countByUserId(userId);
        return count > 0;
    }

    public boolean isDuplicatedEmail(String email) {
        int isExistEmail = userRepository.countByEmail(email);
        if (isExistEmail == 0) {
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    @Transactional
    public void updateBalance(String userEmail, Integer amount) {
        User user = userRepository.findByEmail(userEmail);
        if (user != null) {
            user.setBalance(user.getBalance() + amount);
        }
    }

    // UserService에 경험치 증가, 레벨 업그레이드 메서드 추가
    @Transactional
    public void increaseExp(String userEmail, int amount) {
        User user = userRepository.findByEmail(userEmail);
        if (user != null) {
            user.setExp(user.getExp() + amount);
            if (user.getExp() >= 50) {
                user.setExp(user.getExp() - 50);
                user.setLevel(user.getLevel() + 1);
            }
            userRepository.save(user);
        }
    }


    // 모든 사용자 조회
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    // 6월 11일 닉네임 중복 체크
    private void validateDuplicateNickName(User user){
        User findUser = userRepository.findByNickName(user.getNickName());
        System.out.println(findUser);
        if (findUser != null) {
            throw new IllegalStateException("이미 사용하고 있는 닉네임입니다.");
        }
    }

}
