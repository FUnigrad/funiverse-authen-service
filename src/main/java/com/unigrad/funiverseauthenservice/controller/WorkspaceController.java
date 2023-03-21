package com.unigrad.funiverseauthenservice.controller;

import com.unigrad.funiverseauthenservice.entity.Role;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.entity.Workspace;
import com.unigrad.funiverseauthenservice.payload.response.WorkspaceCreateResponse;
import com.unigrad.funiverseauthenservice.payload.request.WorkspaceCreateRequest;
import com.unigrad.funiverseauthenservice.service.IUserService;
import com.unigrad.funiverseauthenservice.service.IWorkspaceService;
import com.unigrad.funiverseauthenservice.util.DTOConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/workspace")
public class WorkspaceController {

    private final IWorkspaceService workspaceService;

    private final IUserService userService;

    private final DTOConverter dtoConverter;

    private final PasswordEncoder passwordEncoder;

    public WorkspaceController(IWorkspaceService workspaceService, IUserService userService, DTOConverter dtoConverter, PasswordEncoder passwordEncoder) {
        this.workspaceService = workspaceService;
        this.userService = userService;
        this.dtoConverter = dtoConverter;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<Workspace>> getAll() {

        return ResponseEntity.ok(workspaceService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workspace> getById(@PathVariable Long id) {
        Optional<Workspace> workspaceOpt = workspaceService.get(id);

        return workspaceService.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<WorkspaceCreateResponse> save(@RequestBody WorkspaceCreateRequest workspaceDTO) {
        Workspace newWorkspace = workspaceService.save(dtoConverter.convert(workspaceDTO, Workspace.class));

        User admin = User.builder()
                .workspace(newWorkspace)
                .eduMail(workspaceDTO.getEduMail())
                .personalMail(workspaceDTO.getPersonalMail())
                .password(passwordEncoder.encode("123456"))
                .role(Role.WORKSPACE_ADMIN)
                .isActive(true)
                .build();

        WorkspaceCreateResponse result = dtoConverter.convert(newWorkspace, WorkspaceCreateResponse.class);
        result.setAdmin(dtoConverter.convert(userService.save(admin), WorkspaceCreateResponse.Admin.class));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newWorkspace.getId()).toUri();

        return ResponseEntity.created(location).body(result);
    }

    @PutMapping
    public ResponseEntity<Workspace> update(@RequestBody Workspace workspace) {

        return workspaceService.get(workspace.getId())
                .map(w -> ResponseEntity.ok(workspaceService.save(workspace)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!workspaceService.isExist(id)) {
            return ResponseEntity.notFound().build();
        }

        workspaceService.inactivate(id);
        return ResponseEntity.ok().build();
    }
}