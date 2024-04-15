@echo off 

REM
REM  Package ShootVitor on Windows
REM



set VERSION=2.0
set JPACKAGE=G:\Java\JDKs\Azul\zulu17.44.53-ca-fx-jdk17.0.8.1-win_x64\bin\jpackage.exe
set JMODS=G:\Java\JDKs\Azul\zulu17.44.53-ca-fx-jdk17.0.8.1-win_x64\jmods
set TYPE=msi



%JPACKAGE%  ^
 --type "%TYPE%"  ^
 --dest "../output"  ^
 --name "ShootVitor"  ^
 --app-version "%VERSION%"  ^
 --description "Shoot the Vitor - He's a Bich! 2D shooter game by MasterMike."  ^
 --vendor "autumo GmbH"  ^
 --copyright "2024 autumo GmbH"  ^
 --resource-dir "../package"  ^
 --icon "../package/ShootVitor.ico"  ^
 --module-path "%JMODS%"  ^
 --add-modules javafx.controls,javafx.media  ^
 --main-jar "../target/ShootVitor-%VERSION%.jar"  ^
 --main-class "ch.autumo.games.shootvitor.App"  ^
 --input "../target"  ^
 --java-options -Xmx2048m  ^
 --win-menu-group "autumo GmbH"  ^
 --win-dir-chooser  ^
 --win-menu  ^
 --win-shortcut

