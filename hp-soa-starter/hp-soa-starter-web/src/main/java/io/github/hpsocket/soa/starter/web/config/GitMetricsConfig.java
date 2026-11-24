package io.github.hpsocket.soa.starter.web.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.info.GitProperties;
import org.springframework.util.StringUtils;

@AutoConfiguration
@ConditionalOnBean(MeterRegistry.class)
public class GitMetricsConfig
{

    private final MeterRegistry meterRegistry;
    private final GitProperties gitProperties;

    @Value("${BRANCH_OR_TAG:}")
    private String ciBranchOrTag;

    public GitMetricsConfig(MeterRegistry meterRegistry, ObjectProvider<GitProperties> gitPropertiesProvider)
    {
        this.meterRegistry = meterRegistry;
        this.gitProperties = gitPropertiesProvider.getIfAvailable();
    }

    @PostConstruct
    public void registerGitInfoMetric()
    {
        String branch       = "";
        String tag          = "";
        String closestTag   = "";
        String commit       = "";

        if (gitProperties != null)
        {
            branch      = getOrDefault(gitProperties.getBranch(), branch);
            tag         = getOrDefault(gitProperties.get("tags"), tag);
            closestTag  = getOrDefault(gitProperties.get("closest.tag.name"), closestTag);
            commit      = getOrDefault(gitProperties.getShortCommitId(), commit);
        }

        String branchOrTag = !"none".equals(tag) ? tag : branch;
        if (StringUtils.hasText(ciBranchOrTag))
            branchOrTag = ciBranchOrTag.replaceFirst("^origin/", "").replaceFirst("^refs/tags/", "");

        Gauge.builder("app_git_version", () -> 1)
                .description("Application Git branch and tag info from git-commit-id-maven-plugin")
                .tag("branch", branch)
                .tag("tag", tag)
                .tag("closest_tag", closestTag)
                .tag("branch_or_tag", branchOrTag)
                .tag("commit", commit)
                .register(meterRegistry);
    }

    private String getOrDefault(String value, String defaultValue)
    {
        return StringUtils.hasText(value) ? value : defaultValue;
    }

}