package com.example.application;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private static JavaClasses applicationClasses;

    @BeforeAll
    static void importApplicationClasses() {
        // Arrange
        applicationClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.example.application");
    }

    @Test
    void controllers_shouldNotDependDirectlyOnRepositories() {
        // Act and assert
        noClasses()
                .that().haveSimpleNameEndingWith("Controller")
                .should().dependOnClassesThat().haveSimpleNameEndingWith("Repository")
                .because("inbound adapters must delegate to feature services")
                .check(applicationClasses);
    }

    @Test
    void featureServices_shouldNotDependOnConcreteProviderAdapters() {
        // Act and assert
        noClasses()
                .that().haveSimpleNameEndingWith("Service")
                .and().resideOutsideOfPackages("..firebase..", "..noop..")
                .should().dependOnClassesThat().resideInAnyPackage("..firebase..", "..noop..")
                .because("feature services must depend on local provider boundaries")
                .check(applicationClasses);
    }

    @Test
    void concreteEntities_shouldRemainOwnedByFeatures() {
        // Act and assert
        noClasses()
                .that().areAnnotatedWith(Entity.class)
                .should().resideInAPackage("..entity..")
                .because("the entity package contains shared base types, not feature models")
                .check(applicationClasses);
    }
}
