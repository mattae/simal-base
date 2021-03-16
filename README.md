# LAMIS Base module

This module builds on **[Across Framework](https://docs.across.dev/across-site/production/across/index.html)** to add
ability to plug in an **[Across Module](https://docs.across.dev/across-site/production/across/in-a-nutshell.html)** "
dynamically" to an Across Application.

An Across application requires that all modules be defined at compile time; what this project does is to remove that
restriction. Modules can be developed separately and later on added to the application without the need to recompile the
base.

Each module still maintains the definition of an Across module with added external configuration required by this module
to support this dynamic ability. The module configuration is read from the database at start-up, then the module
definition jar file is then read from a database or filesystem and then added to the application classpath and
the ``AcrossContext``. This ability is handled by
``org.lamisplus.modules.base.configurer.DynamicModuleImportConfigurer`` 


