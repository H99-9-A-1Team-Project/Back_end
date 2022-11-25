package com.example.backend.user.service;


import com.example.backend.global.S3.dto.AwsS3;
import com.example.backend.global.S3.service.AmazonS3Service;
import com.example.backend.global.config.auth.UserDetailsImpl;
import com.example.backend.global.entity.Authority;
import com.example.backend.global.entity.Realtor;
import com.example.backend.global.entity.User;
import com.example.backend.global.exception.customexception.common.AccessDeniedException;
import com.example.backend.global.exception.customexception.user.MemberNotFoundException;
import com.example.backend.global.exception.customexception.user.UserUnauthorizedException;
import com.example.backend.mail.MailDto;
import com.example.backend.mail.MailService;
import com.example.backend.user.dto.*;
import com.example.backend.user.repository.RealtorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RealtorService {

    private final RealtorRepository realtorRepository;
    private final AmazonS3Service amazonS3Service;
    private final MailService mailService;

    @Value("${cloud.aws.credentials.domain}")
    private String amazonS3Domain;

    @Transactional
    public void approveRealtor(RealtorApproveDto dto, UserDetailsImpl userDetails) {
        validateManager(userDetails);

        Realtor realtor = realtorRepository.findByEmail(dto.getEmail()).orElseThrow(MemberNotFoundException::new);
        realtor.update(dto);

        sendApproveResultEmail(dto, realtor);
    }

    private void sendApproveResultEmail(RealtorApproveDto dto, Realtor realtor) {
        MailDto mail = new MailDto(realtor.getEmail());

        Long accountCheck = dto.getAccountCheck();
        if(accountCheck == 1) { mail.setRealtorApproveMessage(); }
        else if (accountCheck == 2) { mail.setRealtorRejectMessage(); }

        mailService.sendSimpleMessage(mail);
    }

    @Transactional(readOnly = true)
    public List<RealtorListResponseDto> getRealtorList(UserDetailsImpl userDetails) {
        validateManager(userDetails);

        List<Realtor> realtorList = realtorRepository.findAll();
        return realtorList.stream().map(RealtorListResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void editRealtorProfile(MultipartFile multipartFile, RealtorEditRequestDto realtorEditRequestDto, UserDetailsImpl userDetails) throws IOException {
        validRealtor(userDetails);

        Realtor realtor = realtorRepository.findByEmail(userDetails.getUser().getEmail()).orElseThrow();
        if (multipartFile == null || multipartFile.isEmpty()) {
            realtor.update(realtorEditRequestDto);
            return;
        }

        String imageUrl = amazonS3Service.upload(multipartFile, "realtor-authentication", userDetails.getUser().getEmail());
        realtor.update(realtorEditRequestDto, imageUrl);
    }

    private void validAuth(UserDetailsImpl userDetails){
        if(userDetails == null)
            throw new UserUnauthorizedException();
    }

    private void validRealtor(UserDetailsImpl userDetails){
        if(userDetails.getAuthority() != Authority.ROLE_REALTOR)
            throw new AccessDeniedException();
    }

    private void validateManager(UserDetailsImpl userDetails) {
        validAuth(userDetails);
        if (userDetails.getAuthority() != Authority.ROLE_ADMIN)
            throw new AccessDeniedException();
    }
}


