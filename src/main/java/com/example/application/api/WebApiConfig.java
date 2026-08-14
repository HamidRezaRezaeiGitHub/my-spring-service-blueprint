package com.example.application.api;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.RequestPath;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring 7 API versioning configuration.
 *
 * <p>This configuration enables <strong>path-segment</strong> based API versioning, where the
 * version travels in the URL at segment index {@code 1} (e.g. {@code /api/v1/auth/register} →
 * version {@code "1"}). Combined with {@code @RequestMapping(version = "1")} on individual
 * controllers, the framework resolves and validates versions per request.
 *
 * <h2>Controller mapping convention</h2>
 *
 * <p>Versioned controllers must use the path template
 * <strong>{@code /api/v&#123;version&#125;/&lt;resource&gt;}</strong>. The literal {@code v} prefix
 * is part of the URL template (not the URI variable), so {@code &#123;version&#125;} captures only
 * the numeric portion. This is critical for two reasons:
 * <ul>
 *   <li><strong>Strict matching:</strong> URLs without the {@code v} prefix (e.g. {@code /api/1/auth/...})
 *       do not match the handler. Without the literal {@code v} in the template, Spring's
 *       {@code &#123;version&#125;} URI variable would swallow any segment, and combined with
 *       {setVersionRequired(boolean) version-not-required} mode, both versioned and
 *       bare-numeric URLs would erroneously route to the same handler.</li>
 *   <li><strong>SpringDoc / Swagger UI rendering:</strong> SpringDoc substitutes
 *       {@code &#123;version&#125;} with the supported version string (e.g. {@code "1"}) when
 *       rendering paths. Putting the {@code v} in the literal portion ensures Swagger shows
 *       {@code /api/v1/auth/me} rather than {@code /api/1/auth/me}.</li>
 * </ul>
 *
 * <h2>Opt-in semantics</h2>
 *
 * <ul>
 *   <li>Controllers that declare {@code version = "..."} participate in version resolution and
 *       must use the {@code /api/v&#123;version&#125;/...} template.</li>
 *   <li>Handlers without a {@code version} attribute (for example MCP, OpenAPI, Swagger UI, and
 *       actuator endpoints) are unaffected and continue to match purely on path.</li>
 * </ul>
 *
 * <p>{@link ApiVersionConfigurer#setVersionRequired(boolean) setVersionRequired(false)} is set
 * intentionally and is <strong>not</strong> safe to flip to {@code true} under the current
 * application topology. Spring 7's {@code DefaultApiVersionStrategy#validateVersion} (also used
 * by {@code SpringDocApiVersionStrategy}) does <em>not</em> honor the {@link #VERSIONED_API_PREDICATE}
 * when validating — the predicate only gates path-segment <em>parsing</em>. With required=true,
 * any request without a resolved version (including unversioned namespaces such as
 * {@code /mcp}, {@code /v3/api-docs}, and {@code /actuator/*}) throws
 * {@code MissingApiVersionException}, regardless of the predicate. As long as those unversioned
 * namespaces exist alongside versioned controllers, this flag must remain {@code false} and
 * version-opt-in is enforced solely via the per-controller {@code @RequestMapping(version=...)}
 * attribute combined with the literal {@code v} prefix in the path template.
 *
 * <p>Reference:
 * <a href="https://spring.io/blog/2025/09/16/api-versioning-in-spring">API Versioning in Spring</a>
 * and
 * <a href="https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/api-version.html">MVC config — API version</a>.
 */
@Configuration
public class WebApiConfig implements WebMvcConfigurer {

    /** First migrated version of the public REST API. */
    public static final String API_V1 = "1";

    /**
     * Path-segment versioning only applies to URLs of the form {@code /api/v{N}/...} where
     * {@code N} is one or more digits. All other requests bypass version resolution entirely
     * (the segment is not parsed as a version), so unversioned endpoints such as
     * {@code /mcp} and {@code /actuator/*} continue to work unchanged.
     *
     * <p>The captured segment is the literal {@code v{N}} token; Spring's default
     * {@code SemanticApiVersionParser} strips the leading {@code v}, yielding the version
     * number {@code N} (e.g. {@code "v1" → "1"}).
     */
    private static final Pattern VERSIONED_API_PATH = Pattern.compile("^/api/v\\d+(/.*)?$");

    private static final Predicate<RequestPath> VERSIONED_API_PREDICATE =
            path -> VERSIONED_API_PATH.matcher(path.value()).matches();

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .usePathSegment(1, VERSIONED_API_PREDICATE)
                .addSupportedVersions(API_V1)
                .setVersionRequired(false);
    }
}
