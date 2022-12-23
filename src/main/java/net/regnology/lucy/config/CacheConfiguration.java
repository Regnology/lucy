package net.regnology.lucy.config;

import java.time.Duration;
import net.regnology.lucy.domain.*;
import net.regnology.lucy.repository.UserRepository;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration =
            Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                    .build()
            );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, User.class.getName());
            createCache(cm, Authority.class.getName());
            createCache(cm, User.class.getName() + ".authorities");
            createCache(cm, License.class.getName());
            createCache(cm, License.class.getName() + ".requirements");
            createCache(cm, License.class.getName() + ".libraries");
            createCache(cm, License.class.getName() + ".libraryPublishes");
            createCache(cm, License.class.getName() + ".libraryFiles");
            createCache(cm, LicenseRisk.class.getName());
            createCache(cm, Requirement.class.getName());
            createCache(cm, Requirement.class.getName() + ".licenses");
            createCache(cm, Product.class.getName());
            createCache(cm, Product.class.getName() + ".libraries");
            createCache(cm, LibraryPerProduct.class.getName());
            createCache(cm, LicenseNamingMapping.class.getName());
            createCache(cm, GenericLicenseUrl.class.getName());
            createCache(cm, Upload.class.getName());
            createCache(cm, LicensePerLibrary.class.getName());
            createCache(cm, Library.class.getName());
            //createCache(cm, Library.class.getName() + ".licenses");
            createCache(cm, Library.class.getName() + ".errorLogs");
            createCache(cm, Library.class.getName() + ".licenseToPublishes");
            createCache(cm, Library.class.getName() + ".licenseOfFiles");
            createCache(cm, LibraryErrorLog.class.getName());
            createCache(cm, Fossology.class.getName());
            createCache(cm, License.class.getName() + ".licenseConflicts");
            createCache(cm, LicenseConflict.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
