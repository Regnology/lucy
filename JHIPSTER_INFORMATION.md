# Important information and guidance regarding JHipster changes and upgrades

JHipster dynamically created many files when generating the application using the jhipster-jdl.jdl file.
If changes are made to the .jdl, for example adding new entities or attributes to existing entities,
these files are overwritten again. Likewise during a JHipster upgrade. If customizations have already been made to
these files, they will be lost again.
For this reason a large part of these files were derived, so that only own changes are made there and these are to
be called at all places in each case.
However, it is not always possible to derive files. For this reason, it must be ensured that if JHipster overwrites
these files, the changes must be undone again.

The following files were derived or the original files were changed:

## Default files which needs to be adapted after they have been overwritten

### Back end

### Front end

- module.ts -> Add new custom classes at "declarations" (old entries must not be deleted). Rename *RoutingModule to *RoutingCustomModule. Under "entryComponents" rename *Component to *CustomComponent.
- service/service.ts -> Privat parameter in constructor needs to be changed to protected, otherwise it is not possible to derive this class.

## Extended files

### Back end

- web.rest: New folder **"v1"**. All controller classes have been derived.

### Front end

- service/\* (Changes on default class necessary)
- delete/\*
- detail/\*
- list/\*
- update/\*
