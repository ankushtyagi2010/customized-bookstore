package com.bookstore.controller;

import com.bookstore.dto.AddressDto;
import com.bookstore.dto.ChangePasswordDto;
import com.bookstore.dto.ProfileUpdateDto;
import com.bookstore.model.User;
import com.bookstore.service.CustomizedBookService;
import com.bookstore.service.OrderService;
import com.bookstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final OrderService orderService;
    private final CustomizedBookService customizedBookService;

    public ProfileController(UserService userService, OrderService orderService,
                            CustomizedBookService customizedBookService) {
        this.userService = userService;
        this.orderService = orderService;
        this.customizedBookService = customizedBookService;
    }

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getCurrentUser(userDetails);

        // Profile data
        model.addAttribute("user", user);

        // Statistics
        int totalOrders = orderService.findByUserId(user.getId()).size();
        int totalBooks = customizedBookService.findByUserId(user.getId()).size();
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalBooks", totalBooks);

        // Forms
        ProfileUpdateDto profileDto = ProfileUpdateDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .build();
        model.addAttribute("profileDto", profileDto);
        model.addAttribute("changePasswordDto", new ChangePasswordDto());
        model.addAttribute("addressDto", new AddressDto());

        return "profile/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profileDto") ProfileUpdateDto profileDto,
                                BindingResult result,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (result.hasErrors()) {
            User user = getCurrentUser(userDetails);
            model.addAttribute("user", user);
            model.addAttribute("totalOrders", orderService.findByUserId(user.getId()).size());
            model.addAttribute("totalBooks", customizedBookService.findByUserId(user.getId()).size());
            model.addAttribute("changePasswordDto", new ChangePasswordDto());
            model.addAttribute("addressDto", new AddressDto());
            model.addAttribute("showProfileTab", true);
            return "profile/profile";
        }

        User user = getCurrentUser(userDetails);
        user.setFirstName(profileDto.getFirstName());
        user.setLastName(profileDto.getLastName());
        user.setPhone(profileDto.getPhone());
        userService.updateUser(user);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/profile";
    }

    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDto") ChangePasswordDto passwordDto,
                                 BindingResult result,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        User user = getCurrentUser(userDetails);

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("totalOrders", orderService.findByUserId(user.getId()).size());
            model.addAttribute("totalBooks", customizedBookService.findByUserId(user.getId()).size());
            model.addAttribute("profileDto", ProfileUpdateDto.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phone(user.getPhone())
                    .build());
            model.addAttribute("addressDto", new AddressDto());
            model.addAttribute("showPasswordTab", true);
            return "profile/profile";
        }

        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match!");
            return "redirect:/profile";
        }

        boolean success = userService.changePassword(user.getId(),
                passwordDto.getCurrentPassword(),
                passwordDto.getNewPassword());

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect!");
        }

        return "redirect:/profile";
    }

    @PostMapping("/address/add")
    public String addAddress(@Valid @ModelAttribute("addressDto") AddressDto addressDto,
                             BindingResult result,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        User user = getCurrentUser(userDetails);

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("totalOrders", orderService.findByUserId(user.getId()).size());
            model.addAttribute("totalBooks", customizedBookService.findByUserId(user.getId()).size());
            model.addAttribute("profileDto", ProfileUpdateDto.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phone(user.getPhone())
                    .build());
            model.addAttribute("changePasswordDto", new ChangePasswordDto());
            model.addAttribute("showAddressTab", true);
            return "profile/profile";
        }

        User.Address address = User.Address.builder()
                .label(addressDto.getLabel())
                .street(addressDto.getStreet())
                .city(addressDto.getCity())
                .state(addressDto.getState())
                .zipCode(addressDto.getZipCode())
                .country(addressDto.getCountry())
                .isDefault(addressDto.isDefault() || user.getAddresses().isEmpty())
                .build();

        // If this is set as default, unset others
        if (address.isDefault()) {
            user.getAddresses().forEach(addr -> addr.setDefault(false));
        }

        userService.addAddress(user.getId(), address);
        redirectAttributes.addFlashAttribute("success", "Address added successfully!");
        return "redirect:/profile";
    }

    @PostMapping("/address/delete/{addressId}")
    public String deleteAddress(@PathVariable String addressId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(userDetails);
        userService.deleteAddress(user.getId(), addressId);
        redirectAttributes.addFlashAttribute("success", "Address deleted successfully!");
        return "redirect:/profile";
    }

    @PostMapping("/address/default/{addressId}")
    public String setDefaultAddress(@PathVariable String addressId,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(userDetails);

        // Update default status
        user.getAddresses().forEach(addr -> addr.setDefault(addr.getId().equals(addressId)));
        userService.updateUser(user);

        redirectAttributes.addFlashAttribute("success", "Default address updated!");
        return "redirect:/profile";
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
