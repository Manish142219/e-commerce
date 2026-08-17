package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.NavMenuDto;
import com.ecommerce.service.NavMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nav")
@RequiredArgsConstructor
public class NavMenuController {

    private final NavMenuService navMenuService;

    @GetMapping("/sections")
    public ResponseEntity<ApiResponse<List<String>>> getSections() {
        return ResponseEntity.ok(ApiResponse.success(navMenuService.getNavSections()));
    }

    @GetMapping("/{section}")
    public ResponseEntity<ApiResponse<NavMenuDto>> getNavMenu(@PathVariable String section) {
        return ResponseEntity.ok(ApiResponse.success(navMenuService.getNavMenu(section.toUpperCase())));
    }
}
