
# https://stackoverflow.com/questions/54055598/module-not-found-javafx-controls
# https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html

TYPE=dmg

cd ..

/Volumes/Fastdrive/Development/JDKs/Azul/mac/zulu21.32.17-ca-jdk21.0.2-macosx_x64/bin/jpackage  \
 --type $TYPE  \
 --dest output  \
 --name "ShootVitor"  \
 --app-version "2.0"  \
 --description "Shoot the Vitor - He's a Bich! 2D shooter game by MasterMike."  \
 --vendor "autumo GmbH"  \
 --copyright "2024 autumo GmbH"  \
 --license-file "/Users/Mike/Development/NetBeansProjects/ShootVitor/package/LICENSE.txt"  \
 --resource-dir package  \
 --icon "/Users/Mike/Development/NetBeansProjects/ShootVitor/package/ShootVitor.icns"  \
 --module-path "/Users/Mike/Library/Java/javafx-jmods-21.0.2":"/Users/Mike/Development/NetBeansProjects/ShootVitor/target/classes"  \
 --add-modules javafx.controls,javafx.media  \
 --main-jar "/Users/Mike/Development/NetBeansProjects/ShootVitor/target/ShootVitor-2.0.jar"  \
 --main-class "ch.autumo.games.shootvitor.App"  \
 --input "/Users/Mike/Development/NetBeansProjects/ShootVitor/target"  \
 --mac-package-identifier "ch.autumo.games.shootvitor"  \
 --mac-package-name "ShootVitor 2.0"  \
 --mac-app-category "public.app-category.games"
