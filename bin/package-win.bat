
cd ..

G:\Java\JDKs\Azul\zulu17.44.53-ca-fx-jdk17.0.8.1-win_x64\bin\jpackage.exe  ^
 --verbose  ^
 --type msi  ^
 --dest output  ^
 --name "ShootVitor"  ^
 --app-version "2.0"  ^
 --description "Shoot the Vitor - He's a Bich! 2D shooter game by MasterMike."  ^
 --vendor "autumo GmbH"  ^
 --copyright "2024 autumo GmbH"  ^
 --resource-dir package  ^
 --icon "C:\Users\mike\Desktop\ShootVitor\package\ShootVitor.ico"  ^
 --module-path "G:\Java\JDKs\Azul\zulu17.44.53-ca-fx-jdk17.0.8.1-win_x64\jmods";"C:\Users\mike\Desktop\ShootVitor\target\classes"  ^
 --add-modules javafx.controls,javafx.media  ^
 --input "target"  ^
 --main-jar "ShootVitor-2.0.jar"  ^
 --main-class "ch.autumo.games.shootvitor.App"  ^
 --input "C:\Users\mike\Desktop\ShootVitor\target"  ^
 --java-options -Xmx2048m  ^
 --win-menu-group "autumo GmbH"  ^
 --win-dir-chooser  ^
 --win-menu  ^
 --win-shortcut
