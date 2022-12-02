package com.example.backend.chat.model;

import com.example.backend.user.model.User;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ChatRoomMember {
    private static final long serialVersionUID = 6494678977089006639L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMemberNo;

    @JoinColumn(name = "roomNo", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @JoinColumn(name = "UserNo", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private Boolean enterStatus;


    public ChatRoomMember() {

    }

    @Builder
    public ChatRoomMember(Long roomMemberNo, ChatRoom chatRoom, Member member, Boolean enterStatus) {
        this.roomMemberNo = roomMemberNo;
        this.chatRoom = chatRoom;
        this.member = member;
        this.enterStatus = enterStatus;
    }

    public void updateEnterStatus() {
        this.enterStatus = true;
    }
}