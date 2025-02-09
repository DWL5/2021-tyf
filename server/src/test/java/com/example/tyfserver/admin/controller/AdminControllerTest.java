package com.example.tyfserver.admin.controller;

import com.example.tyfserver.admin.dto.*;
import com.example.tyfserver.admin.exception.InvalidAdminException;
import com.example.tyfserver.admin.service.AdminService;
import com.example.tyfserver.auth.config.AuthenticationArgumentResolver;
import com.example.tyfserver.auth.config.AuthenticationInterceptor;
import com.example.tyfserver.auth.config.RefundAuthenticationArgumentResolver;
import com.example.tyfserver.auth.dto.TokenResponse;
import com.example.tyfserver.member.exception.MemberNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureRestDocs
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;
    @MockBean
    private AuthenticationArgumentResolver authenticationArgumentResolver;
    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;
    @MockBean
    private RefundAuthenticationArgumentResolver refundAuthenticationArgumentResolver;

    @Test
    @DisplayName("/admin/account/approve/{memberId} - success")
    public void approveAccount() throws Exception {
        //given
        when(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        doNothing().when(adminService).approveAccount(Mockito.anyLong());
        //when
        //then
        mockMvc.perform(post("/admin/account/approve/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("approveAccount",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @DisplayName("/admin/account/approve/{memberId} - fail - member not found")
    public void approveAccountFailWhenMemberNotFound() throws Exception {
        //when
        when(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        doThrow(new MemberNotFoundException()).when(adminService).approveAccount(anyLong());
        //then
        mockMvc.perform(post("/admin/account/approve/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(document("approveAccountFailWhenMemberNotFound",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @DisplayName("계좌 등록 요청 반려")
    public void rejectAccount() throws Exception {
        //given
        AccountRejectRequest request = new AccountRejectRequest("테스트취소사유");
        //when
        when(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        doNothing().when(adminService).rejectAccount(Mockito.anyLong(), Mockito.any());
        //then
        mockMvc.perform(post("/admin/account/reject/1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("rejectAccount",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @DisplayName("계좌 등록 요청 반려 - 실패 - member not found")
    public void rejectAccountFailMemberNotFound() throws Exception {
        //given
        AccountRejectRequest request = new AccountRejectRequest("테스트취소사유");
        //when
        when(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        doThrow(new MemberNotFoundException()).when(adminService).rejectAccount(anyLong(), any());

        //then
        mockMvc.perform(post("/admin/account/reject/1")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value(MemberNotFoundException.ERROR_CODE))
                .andDo(document("rejectAccountFailMemberNotFound",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @DisplayName("계좌 등록 요청 목록 조회")
    public void requestingAccounts() throws Exception {
        //given
        List<RequestingAccountResponse> responses = new ArrayList<>();
        responses.add(new RequestingAccountResponse(1L, "test1@test.com", "nickname1", "pagename1", "accountholder1",
                "1234-1234-12341", "bank", "https://test.test.png"));
        responses.add(new RequestingAccountResponse(2L, "test2@test.com", "nickname2", "pagename2", "accountholder2",
                "1234-1234-12342", "bank", "https://test.test.png"));

        //when
        when(authenticationInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        when(adminService.findRequestingAccounts()).thenReturn(responses);
        //then
        mockMvc.perform(get("/admin/list/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..memberId").exists())
                .andExpect(jsonPath("$..nickname").exists())
                .andExpect(jsonPath("$..accountHolder").exists())
                .andExpect(jsonPath("$..accountNumber").exists())
                .andExpect(jsonPath("$..bank").exists())
                .andExpect(jsonPath("$..bankbookImageUrl").exists())
                .andExpect(jsonPath("$[0].accountNumber").value("1234-1234-12341"))
                .andDo(document("requestingAccounts",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @DisplayName("정산 신청 목록 조회")
    public void exchangeList() throws Exception {
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        when(adminService.exchangeList())
                .thenReturn(singletonList(
                        new ExchangeResponse("승윤", "tyf@gmail.com", "nickname", "pagename", 10000L, LocalDateTime.now(), "123-123")
                ));

        mockMvc.perform(get("/admin/list/exchange")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].exchangeAmount").value(10000L))
                .andExpect(jsonPath("$[0].accountNumber").value("123-123"))
                .andExpect(jsonPath("$[0].nickname").value("nickname"))
                .andExpect(jsonPath("$[0].pageName").value("pagename"))
                .andDo(print())
                .andDo(document("exchangeList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @DisplayName("정산 승인")
    public void approveExchange() throws Exception {
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        doNothing().when(adminService).approveExchange(anyString());

        mockMvc.perform(post("/admin/exchange/approve/pagename")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("approveExchange",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @DisplayName("정산 승인 - 회원을 찾을 수 없음")
    public void approveExchangeMemberNotFound() throws Exception {
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        doThrow(new MemberNotFoundException()).when(adminService).approveExchange(anyString());

        mockMvc.perform(post("/admin/exchange/approve/pagename")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value(MemberNotFoundException.ERROR_CODE))
                .andDo(print())
                .andDo(document("approveExchangeMemberNotFound",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @DisplayName("정산 거절")
    public void rejectExchange() throws Exception {
        ExchangeRejectRequest request = new ExchangeRejectRequest("pagename", "no reason just fun");
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        doNothing().when(adminService).rejectExchange(anyString(), anyString());

        mockMvc.perform(post("/admin/exchange/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("rejectExchange",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @DisplayName("정산 거절 - 회원을 찾을 수 없음")
    public void rejectExchangeMemberNotFound() throws Exception {
        ExchangeRejectRequest request = new ExchangeRejectRequest("pagename", "no reason just fun");
        when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        doThrow(new MemberNotFoundException()).when(adminService).rejectExchange(anyString(), anyString());

        mockMvc.perform(post("/admin/exchange/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value(MemberNotFoundException.ERROR_CODE))
                .andDo(print())
                .andDo(document("rejectExchangeMemberNotFound",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @DisplayName("/admin/login - success")
    public void login() throws Exception {
        //given
        when(adminService.login(Mockito.any()))
                .thenReturn(new TokenResponse("token"));

        //when //then
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AdminLoginRequest("id", "password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andDo(print())
                .andDo(document("adminLogin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;
    }

    @Test
    @DisplayName("/admin/login - UnregisteredAdminMember Failed")
    public void loginUnregisteredMemberFailed() throws Exception {
        //given
        doThrow(new InvalidAdminException())
                .when(adminService).login(Mockito.any());

        //when //then
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AdminLoginRequest("id", "password"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value(InvalidAdminException.ERROR_CODE))
                .andDo(print())
                .andDo(document("adminLoginUnregisteredMemberFailed",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
        ;
    }
}
