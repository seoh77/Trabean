package com.trabean.travel.service;

import com.trabean.travel.dto.request.InvitaionRequestDto;
import com.trabean.travel.entity.Invitation;
import com.trabean.travel.entity.KrwTravelAccount;
import com.trabean.travel.repository.InvitationRepository;
import com.trabean.travel.repository.KrwTravelAccountRepository;
import java.sql.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final InvitationRepository invitationRepository;
    private final KrwTravelAccountRepository krwTravelAccountRepository;

    @Transactional
    public void invite(InvitaionRequestDto invitaionRequestDto) {
        Long accountId = invitaionRequestDto.getAccountId();
        KrwTravelAccount account = krwTravelAccountRepository.findByAccountId(accountId);

        Invitation invitation = new Invitation();
        invitation.setEmail(invitaionRequestDto.getEmail());
        invitation.setAccepted(false);
        invitation.setInviteDate(new Timestamp(System.currentTimeMillis()));
        invitation.setAccount(account);

        invitationRepository.save(invitation);
    }

}
