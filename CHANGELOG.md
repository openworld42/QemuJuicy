## QemuJuicy - QEMU Java User Interface Contributed Yet

Details to start with: [README.md](https://github.com/openworld42/QemuJuicy/blob/master/README.md)

## Changelog

## upcoming version

### Fixes

### Features/Enhancements

added: sound method/card (ComboBox + QEMU call), help

QemuJuicy help (Menu -> Help, Button)

added: copy QEMU call/parameters to clipboard (button + menu)

added: store QEMU call/parameters to file (to create a shell script or bat file)

## v0.5.1 "Axolotl"

### Fixes

added missing logging: rename VM
License text fix in About (About tab)
missing QEMU parameter when defining the complete QEMU call by hand

### Features/Enhancements
	
Advanced VM Tab added to tabbed pane (to set any QEMU option or the complete QEMU call by hand)

## v0.4.4

### Fixes

### Features/Enhancements

added: rename VM (using name text field)

QEMU boot menu flag (enable/disable boot menu starting a VM with QEMU)

verbose flag (enable/disable verbose stdout messages) implemented in both application and VMs

Added most used architectures:
* qemu-system-x86_64, qemu-system-i386 (have been implemented already)
* qemu-system-aarch64
* qemu-system-arm
* qemu-system-avr
* qemu-system-mips64
* qemu-system-ppc64
* qemu-system-riscv64
* qemu-system-sparc64

## v0.4.1

### "first light" - a preliminary working version

### Fixes

* some (but there is no release jar file published until now, no need to note them)

### Features/Enhancements

VM one-time installation run (+ start button + menu completion)

VM process watcher thread (enable/disable GUI components)

VM properties completed: architecture, accelerator, CPUs/NUMA, memory

## v0.4.0

### Fixes

* a lot (but there is no release jar file published, no need to note them)

### Features/Enhancements

* Added first VM properties (GUI only): architecture, accelerator, CPUs/NUMA

* Added move-up and move-down buttons for moving VMs in the display list

* implementation of the "remove" button for a VM - both just entry and its created disk

* Changed many icons for better aliasing when shrunk

* Added an executor for OS specific process execution and the resulting output (e.g. qemu-img, looking for the version)

* Added a VM wizard (first, small version) with some VM properties to create VMs

* Added a setup wizard for finding the QEMU installation and QemuJuicy VM properties, deleted the old setup dialog

## v0.0.4

### Fixes
### Features/Enhancements

* upload of 1st version, very early alpha 




