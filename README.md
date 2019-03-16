```
  .-_'''-.       ,-----.     ________  ________ .-./`)  
 '_( )_   \    .'  .-,  '.  |        ||        |\ .-.') 
|(_ o _)|  '  / ,-.|  \ _ \ |   .----'|   .----'/ `-' \ 
. (_,_)/___| ;  \  '_ /  | :|  _|____ |  _|____  `-'`"` 
|  |  .-----.|  _`,/ \ _/  ||_( )_   ||_( )_   | .---.  
'  \  '-   .': (  '\_/ \   ;(_ o._)__|(_ o._)__| |   |  
 \  `-'`   |  \ `"/  \  ) / |(_,_)    |(_,_)     |   |  
  \        /   '. \_/``".'  |   |     |   |      |   |  
   `'-...-'      '-----'    '---'     '---'      '---'  
```

[![Build Status](https://travis-ci.org/dzhemriza/goffi.svg?branch=master)](https://travis-ci.org/dzhemriza/goffi)

This is a collection of couple of small file encryption projects. Please find the list below:

* **Moffi** - Notepad like text editor
* **Toffi** - CLI tool for file/directory encryption
* **Text Encoder** - Visual text encryption
* **My Vault** - Notes and passwords store

# Build
To compile goffi you will need OpenJDK 11 installed and Apache Maven 3.5.4+.

```Bash
mvn clean package
```

## Java 11 Notes
Download OpenJDK 11 and OpenJFX

**Create JRE containing all required modules (Windows)**

```
cd <path-to-openjdk-11>\bin
jlink --module-path <path-to-javafx-jmods-11>  ^
      --add-modules java.base,java.scripting,javafx.controls,java.desktop,java.logging,java.management,java.sql,java.naming ^
      --output <path-to-new-jre>
```

# License
This project is licensed under the terms of [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
For more information please see the ```LICENSE``` file.

# Authors
Dzhem Riza
