package com.project.VisitBusan.controller.adminPage;


import com.project.VisitBusan.dto.PageRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@PostAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
@RequestMapping("/admin/report")
public class ReportManagementController {

    @GetMapping("/list")
    public String reportPage(PageRequestDTO pageRequestDTO,
                             Model model) {

        return "adminPage/report/list";
    }

}
