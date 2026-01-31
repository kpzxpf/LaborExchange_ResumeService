package com.vlz.laborexchange_resumeservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "${spring.clients.user-service.name}",
        url = "${spring.clients.user-service.url}"
)
public interface RoleServiceClient {
    @GetMapping("/api/roles/roleById")
    String getUserRoleById(@RequestParam("id") Long id);
}
