package com.markethub.ecommerce.controller;
import com.markethub.ecommerce.dto.ApiResponse;
import com.markethub.ecommerce.entity.*;
import com.markethub.ecommerce.exception.AppException;
import com.markethub.ecommerce.repository.*;
import com.markethub.ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/users") @RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepo;
    private final AddressRepository addressRepo;
    private final AuthService authService;

    private String uid() { return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(); }

    @GetMapping("/profile") public ResponseEntity<?> profile() {
        var user = userRepo.findById(uid()).orElseThrow();
        return ResponseEntity.ok(ApiResponse.ok(authService.mapUser(user)));
    }

    @PutMapping("/profile") public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body) {
        var user = userRepo.findById(uid()).orElseThrow();
        if (body.containsKey("name")) user.setName(body.get("name"));
        if (body.containsKey("phone")) user.setPhone(body.get("phone"));
        if (body.containsKey("avatar")) user.setAvatar(body.get("avatar"));
        return ResponseEntity.ok(ApiResponse.ok(authService.mapUser(userRepo.save(user))));
    }

    @GetMapping("/addresses") public ResponseEntity<?> addresses() { return ResponseEntity.ok(ApiResponse.ok(addressRepo.findByUserId(uid()))); }

    @PostMapping("/addresses") public ResponseEntity<?> addAddress(@RequestBody Map<String, String> body) {
        var addr = Address.builder().userId(uid()).name(body.get("name")).phone(body.get("phone"))
            .line1(body.get("line1")).line2(body.get("line2")).city(body.get("city")).state(body.get("state"))
            .pincode(body.get("pincode")).label(Address.Label.valueOf(body.getOrDefault("label", "HOME").toUpperCase())).build();
        return ResponseEntity.ok(ApiResponse.ok(addressRepo.save(addr)));
    }

    @PutMapping("/addresses/{id}") public ResponseEntity<?> updateAddress(@PathVariable String id, @RequestBody Map<String, String> body) {
        var addr = addressRepo.findById(id).orElseThrow(() -> AppException.notFound("Address not found"));
        if (body.containsKey("name")) addr.setName(body.get("name"));
        if (body.containsKey("phone")) addr.setPhone(body.get("phone"));
        if (body.containsKey("line1")) addr.setLine1(body.get("line1"));
        if (body.containsKey("line2")) addr.setLine2(body.get("line2"));
        if (body.containsKey("city")) addr.setCity(body.get("city"));
        if (body.containsKey("state")) addr.setState(body.get("state"));
        if (body.containsKey("pincode")) addr.setPincode(body.get("pincode"));
        return ResponseEntity.ok(ApiResponse.ok(addressRepo.save(addr)));
    }

    @DeleteMapping("/addresses/{id}") public ResponseEntity<?> deleteAddress(@PathVariable String id) {
        addressRepo.deleteById(id); return ResponseEntity.ok(ApiResponse.ok(null, "Deleted"));
    }

    @PatchMapping("/addresses/{id}/default") public ResponseEntity<?> setDefault(@PathVariable String id) {
        var addrs = addressRepo.findByUserId(uid());
        addrs.forEach(a -> { a.setDefault(a.getId().equals(id)); });
        addressRepo.saveAll(addrs);
        return ResponseEntity.ok(ApiResponse.ok(null, "Default set"));
    }
}
