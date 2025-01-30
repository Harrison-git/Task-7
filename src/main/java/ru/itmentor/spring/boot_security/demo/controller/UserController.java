package ru.itmentor.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.RoleService;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService)
    {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String getUser(Model model, Principal principal) {
        model.addAttribute("user", userService.findByName(principal.getName()).get());
        return "users/user";
    }

    @GetMapping("/edit")
    public String editUserForm(@RequestParam("id") Long id, Model model) {
        Optional<User> userById = userService.findById(id);

        if (userById.isPresent()) {
            model.addAttribute("user", userById.get());
            model.addAttribute("listRole", roleService.findAll());
            return "users/edit";
        } else {
            return "redirect:/admin/";
        }
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute("user") @Valid User user,
                           BindingResult bindingResult, Model model) {
        try {
            userService.updateUser(user);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("name", "error name", "Попробуйте другое имя");
            model.addAttribute("listRole", roleService.findAll());
            user.setRoles(user.getRoles());
            return "users/edit";
        }
        return "redirect:/admin/";
    }
}