# QemuJuicy

### QEMU Java User Interface Contributed Yet

**QemuJuicy** is a graphical user interface to run [QEMU](https://github.com/qemu/qemu) (a virtual machine emulator). QEMU runs on many operating systems and on a lot of CPUs by software emulation - e.g. Linux on Windows, Windows on Unix, Suse under Ubuntu, Debian on macOS, all on Linux, ARM CPUs, and others.

**QEMU** is used to create and run VMs (short for virtual machines).

**QemuJuicy** is Open Source Software (FOSS, see licenses) and a Java(tm) application running on many OS/machines, QEMU is also Open Source (GPL2).

Preconditions to use **QemuJuicy**:

* **Java** installed (from your package manager or from [here](https://openjdk.java.net/) (Linux, Windows, macOS, others).
* **QEMU** installed (package manager or from [here](https://www.qemu.org) - Linux, Windows, macOS, source)
* **QemuJuicy** JAR file: qemujuicy_vN.N.N.jar (N.N.N is the version) from [github.com](https://github.com/openworld42/QemuJuicy) or package manager
* (an operating system, e.g. as a *.iso file or CD/DVD/Live-CD, that yout want to run or install as the VM)

Run **QemuJuicy** on the command line (or create a menu/desktop entry or a shell script) using:

**java -jar qemujuicy_vN.N.N.jar**  

### QEMU program selection

**QEMU** supports several machines. Therefore, it is important to set the default QEMU program (executable). If **QemuJuicy** is started the first time, it will ask for the default program to emulate the VM (e.g.: *qemu-system-x86_64*, a PC type 64-bit processor). You may change this later under the menu item *File -> QEMU Setup*, searching the file system for other QEMU executables.

### QemuJuicy settings

The menu item *File -> Settings* lets you go through the defaults/presets of *QemuJuicy*. Example: the default number of processors or the default amount of memory for a virtual machine (VM). You may change this later individually for each VM.

**Important**: the location/folder settings, since VM disks (a file) are usually huge (sometimes several GB), containing the files of the VM operating system. You may or may not choose a directory that is part of your backup strategy.

The settings are stored in under your personal user directory: *user-John-Doe -> .qemujuicy -> some-files*. There is also a log file stored - for bug fixing, if you ask for a fix.



