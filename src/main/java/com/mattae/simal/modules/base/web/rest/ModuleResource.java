package com.mattae.simal.modules.base.web.rest;

import com.mattae.simal.modules.base.domain.entities.Module;
import com.mattae.simal.modules.base.domain.entities.WebRemote;
import com.mattae.simal.modules.base.domain.repositories.ModuleRepository;
import com.mattae.simal.modules.base.domain.repositories.WebRemoteRepository;
import com.mattae.simal.modules.base.services.ModuleService;
import com.mattae.simal.modules.base.services.dto.ModuleDependencyDTO;
import com.mattae.simal.modules.base.web.vm.ModuleVM;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ModuleResource {
    private final ModuleRepository moduleRepository;
    private final WebRemoteRepository webRemoteRepository;
    private final ModuleService moduleService;

    @GetMapping("/modules/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ModuleVM> getModule(@PathVariable("id") UUID id) {
        return ResponseUtil.wrapOrNotFound(moduleService.getModule(id));
    }

    @GetMapping("/modules")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Cacheable("modules")
    public List<ModuleVM> getModules() {
        return moduleService.getModules();
    }

    @PostMapping("/modules/activate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @CacheEvict({"modules", "webRemotes"})
    public ModuleVM activateModule(@RequestBody Module module) {
        return moduleService.activate(module);
    }

    @PostMapping("/modules/deactivate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @CacheEvict({"modules", "webRemotes"})
    public ModuleVM deactivateModule(@RequestBody Module module) {
        return moduleService.deactivate(module);
    }

    @GetMapping("/modules/{id}/uninstall")
    @CacheEvict({"modules", "webRemotes"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void uninstallModule(@PathVariable UUID id) {
        moduleService.uninstall(id);
    }

    @PostMapping("/modules/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @CacheEvict({"modules", "webRemotes"})
    public ModuleVM updateModule(@RequestBody Module module) {
        return moduleService.installOrUpdate(module);
    }

    @PostMapping("/modules/upload")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Module uploadModuleData(@RequestParam("file") MultipartFile file) throws Exception {
        return moduleService.uploadModuleData(file);
    }

    @PostMapping("/modules/install")
    @CacheEvict({"modules", "webRemotes"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModuleVM installModule(final @RequestBody Module module) {
        return moduleService.installOrUpdate(module);
    }

    @GetMapping("/modules/{id}/dependencies")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ModuleDependencyDTO> getDependencies(@PathVariable UUID id) {
        return moduleService.getDependencies(id);
    }

    @GetMapping("/modules/{id}/dependents")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ModuleDependencyDTO> getDependents(@PathVariable UUID id) {
        return moduleService.getDependents(id);
    }

    @GetMapping("/modules/web-remotes")
    @Cacheable(cacheNames = "webRemotes")
    public List<WebRemote> getWebRemotes() {
        return moduleRepository.findByStartedIsTrue().stream()
            .flatMap(module -> webRemoteRepository.findByModule(module).stream())
            .collect(Collectors.toList());
    }
}
