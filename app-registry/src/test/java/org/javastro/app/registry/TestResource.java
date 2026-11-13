package org.javastro.app.registry;

import org.ivoa.dm.execution.*;
import org.ivoa.dm.pdl.*;
import org.ivoa.dm.tool.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Builds a representative {@link ExecutionModel} instance exercising most of the execution, tool and pdl models.
 */
public class TestResource {

    private static final ZonedDateTime T0 = ZonedDateTime.of(2026, 3, 1, 9, 0, 0, 0, ZoneOffset.UTC);

    OCIRegistry harbor = OCIRegistry.createOCIRegistry(r -> {
       r.host = "harbor.example.org";
       r.project = "vo-tools";
    });
    OCIRegistry mirror = OCIRegistry.createOCIRegistry(r -> {
        r.host = "mirror.jbo.example.org";
        r.project = "vo-tools";
        r.proxyOf = "https://harbor.example.org";
    });

    Author author = Author.createAuthor(a -> {
        a.name = "Paul Harrison";
        a.email = "paul.harrison@manchester.ac.uk";
        a.github = "pahjbo";
        a.gitlab = "pharrison";
        a.orcid = "0000-0000-0000-0000";
        a.role = "maintainer";
        a.affiliation = "University of Manchester";
    });

    public ToolImage createBaseImage() {
        return ToolImage.createToolImage(i -> {
            i.registries = List.of(harbor);
            i.repository = "base/python";
            i.tag = "3.12";
            i.digest = "sha256:0f1e2d3c4b5a69788796a5b4c3d2e1f00f1e2d3c4b5a69788796a5b4c3d2e1f0";
            i.build = BuildProcess.createBuildProcess(b -> b.buildCommand = "docker build .");
            i.discovery = Discovery.createDiscovery(d -> {
                d.imageTitle = "Python base image";
                d.description = "Certified Python base image";
                d.source = "https://git.example.org/base/python";
                d.version = "3.12.1";
                d.authors = List.of(author);
                d.licenses = "Apache-2.0";
                d.keyword = List.of("base-image");
                d.domain = List.of("astronomy");
                d.kind = List.of("base");
                d.software = List.of("python");
                d.isBase = true;
            });
            i.security = SecurityMetadata.createSecurityMetadata(s -> {
            });
        });
    }

    public ToolImage createImage(ToolImage baseImage){
        return ToolImage.createToolImage(i -> {
            i.registries = List.of(harbor, mirror);
            i.repository = "imager/wsclean";
            i.tag = "latest";
            i.digest = "sha256:a1b2c3d4e5f60718293a4b5c6d7e8f90a1b2c3d4e5f60718293a4b5c6d7e8f90";
            i.platforms = List.of(
                    new ImagePlatform("linux", "amd64", "sha256:1111111111111111111111111111111111111111111111111111111111111111"),
                    new ImagePlatform("linux", "arm64", "sha256:2222222222222222222222222222222222222222222222222222222222222222"));
            i.parentDigest = baseImage.getDigest();
            i.build = BuildProcess.createBuildProcess(b -> {
                b.buildCommand = "docker build -f docker/Dockerfile .";
                b.buildContext = "./";
                b.dockerfilePath = "docker/Dockerfile";
            });
            i.discovery = Discovery.createDiscovery(d -> {
                d.imageTitle = "Radio imager";
                d.description = "Wide-field radio interferometric imager";
                d.source = "https://git.example.org/imager";
                d.url = "https://imager.example.org";
                d.documentation = "https://imager.example.org/docs";
                d.version = "3.2.0";
                d.revision = "9f8e7d6";
                d.created = T0.minusDays(7);
                d.authors = List.of(author);
                d.licenses = "GPL-3.0-or-later";
                d.keyword = List.of("imaging", "radio", "interferometry");
                d.domain = List.of("astronomy");
                d.kind = List.of("application");
                d.software = List.of("wsclean", "casacore");
                d.isBase = false;
                d.deprecated = false;
            });
            i.security = SecurityMetadata.createSecurityMetadata(s -> {
                s.vulnerabilities = List.of(Vulnerability.createVulnerability(v -> {
                    v.cve = "CVE-2025-0001";
                    v.severity = "LOW";
                    v.scannedAt = T0.minusDays(1);
                }));
                s.compliance = List.of(Compliance.createCompliance(c -> {
                    c.signature = "MEUCIQDexample";
                    c.signer = "https://harbor.example.org/signers/vo-tools";
                    c.sbomUrl = "https://harbor.example.org/sbom/imager-3.2.0.json";
                    c.provenanceUrl = "https://harbor.example.org/provenance/imager-3.2.0.json";
                }));
            });
        });
    }

    public Tool createTool(ToolImage image){
        // ---- tool interface
        ParameterDefinition ms = ParameterDefinition.createParameterDefinition(p -> {
            p.name = "visibilities";
            p.description = "Input measurement set";
            p.kind = new FileParameterType("application/x-fits");
            // RDB mapping requires a non-null discriminator for the polymorphic default even though it is optional
            p.defaultValue = new StringLiteral("");
            p.encoding = ValueEncoding.URI;
            p.semantics = new NullSemantic();
        });
        ParameterDefinition size = ParameterDefinition.createParameterDefinition(p -> {
            p.name = "imageSize";
            p.description = "Image size in pixels";
            p.kind = new AtomicParameterType("int");
            p.defaultValue = new IntegerLiteral(1024);
            p.semantics = new NullSemantic();
        });
        ParameterDefinition weighting = ParameterDefinition.createParameterDefinition(p -> {
            p.name = "weighting";
            p.description = "Visibility weighting scheme";
            p.kind = new AtomicParameterType("string");
            p.defaultValue = new StringLiteral("briggs");
            p.encoding = ValueEncoding.PLAIN;
            p.semantics = new NullSemantic();
        });
        ParameterDefinition verbose = ParameterDefinition.createParameterDefinition(p -> {
            p.name = "verbose";
            p.kind = new AtomicParameterType("string");
            p.defaultValue = new BooleanLiteral(false);
            p.semantics = new NullSemantic();
        });
        ParameterDefinition imageOut = ParameterDefinition.createParameterDefinition(p -> {
            p.name = "image";
            p.description = "Output image";
            p.kind = new FileParameterType("application/x-fits");
            p.semantics = new IVOASemantics("phot.flux.density", null);
            p.defaultValue = new StringLiteral("");
            p.encoding = ValueEncoding.URI;
        });

        ParameterGroup inputGroup = ParameterGroup.createParameterGroup(g -> {
            g.name = "inputs";
            g.members = List.of(
                    ParameterOrGroup.createParameterOrGroup(m -> {
                        m.param = ms;
                        m.required = true;
                        m.minItems = 1;
                        m.maxItems = 1;
                    }),
                    ParameterOrGroup.createParameterOrGroup(m -> m.param = size),
                    ParameterOrGroup.createParameterOrGroup(m -> m.param = weighting),
                    ParameterOrGroup.createParameterOrGroup(m -> m.param = verbose));
        });
        ParameterGroup outputGroup = ParameterGroup.createParameterGroup(g -> {
            g.name = "outputs";
            g.members = List.of(ParameterOrGroup.createParameterOrGroup(m -> {
                m.param = imageOut;
                m.required = true;
            }));
        });

        return Tool.createTool(t -> {
            t.tool_id = "imager-3.2.0";
            t.version = "3.2.0";
            t.image = image;
            t.command = List.of("wsclean", "-size", "{{inputs.imageSize}}", "{{inputs.imageSize}}",
                    "-weight", "{{inputs.weighting}}", "/inputs/visibilities");
            t.env = List.of(new EnvVar("OMP_NUM_THREADS", "4"), new EnvVar("TMPDIR", "/tmp"));
            t.toolInterface = ToolInterface.createToolInterface(ti -> {
                ti.parameters = List.of(ms, size, weighting, verbose, imageOut);
                ti.groups = List.of(inputGroup, outputGroup);
                ti.input = inputGroup;
                ti.output = outputGroup;
            });
            t.performanceProfile = PerformanceProfile.createPerformanceProfile(p -> {
                p.processorScaling = ProcessorScaling.createProcessorScaling(s -> {
                });
                p.dataScaling = DataScaling.createDataScaling(s -> {
                });
            });
        });
    }
}
