package io.netpulse.cloud.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "io.netpulse.cloud", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
    @ArchTest
    static final ArchRule domain_has_no_spring = noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework..");

    @ArchTest
    static final ArchRule domain_has_no_jpa = noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..", "javax.persistence..");

    @ArchTest
    static final ArchRule application_does_not_depend_on_outer_layers = noClasses().that()
            .resideInAPackage("..application..").should().dependOnClassesThat()
            .resideInAnyPackage("..infrastructure..", "..api..");

    @ArchTest
    static final ArchRule controllers_do_not_call_repositories = noClasses().that()
            .resideInAPackage("..api.controller..").should().dependOnClassesThat()
            .resideInAnyPackage("..infrastructure.persistence..", "..domain.port..");

    @ArchTest
    static final ArchRule infrastructure_does_not_depend_on_api = noClasses().that()
            .resideInAPackage("..infrastructure..").should().dependOnClassesThat().resideInAPackage("..api..");

    @ArchTest
    static final ArchRule api_does_not_depend_on_persistence = noClasses().that()
            .resideInAPackage("..api..").should().dependOnClassesThat().resideInAPackage("..infrastructure.persistence..");

    @ArchTest
    static final ArchRule no_cycles = SlicesRuleDefinition.slices()
            .matching("io.netpulse.cloud.(*)..").should().beFreeOfCycles();
}
