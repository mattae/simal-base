package com.mattae.simal.modules.base.installers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.mattae.simal.modules.base.domain.entities.Translation;
import com.mattae.simal.modules.base.domain.repositories.TranslationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

@Installer(description = "Installs the default language translations", version = 1, phase = InstallerPhase.AfterModuleBootstrap)
@RequiredArgsConstructor
public class DefaultTranslationsInstaller {
    private final TranslationsRepository translationsRepository;

    @InstallerMethod
    public void install() {
        Translation translation = new Translation();
        translation.setLang("en");
        Resource resource = new ClassPathResource("installers/lamis-base/lang_en.json");
        try {
            String data = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
            JsonNode node = new ObjectMapper().readTree(data);
            translation.setData(node);
            translationsRepository.save(translation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
