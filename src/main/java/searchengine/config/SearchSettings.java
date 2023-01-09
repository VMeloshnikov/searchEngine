package searchengine.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Component
@ConfigurationProperties(prefix = "settings")
public class SearchSettings {
    private String prefix;
    private String agent;
    private String webinterfaceLogin;
    private String webinterfacePassword;
    private String webinterface;

}
