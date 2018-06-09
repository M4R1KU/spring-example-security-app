# Modul 183
## Florian Bürgi & Mario Kunz

## Inhalt
1. Bausteine
  1. H2
  2. Spring
  3. Thymeleaf
2. Datenstruktur
3. Spring Security Konfiguration
4. Login
5. SSL
6. Systemkommando

## Bausteine
In den folgenden Unterkapiteln wird der verwendete Technologie Stack erklärt. Die Gesmatarchitektur richtet sich nach MVC, daher besteht die Applikation auch aus drei Bausteinen. 
### H2
Als Datenbank wird die H2 Database Engine verwendet.

H2 kann direkt in die Anwendung eingebunden werden und muss, daher nicht auf dem Server installiert werden.
Zudem ist das ganze System sehr schlank und effizient gebaut, was für eine erhöhte Performance sorgt.
H2 besitzt aufgrund der Schlankheit einige Features von anderen Datenbankmanagementsystemen nicht, wie zum Beispiel "Stored Procedures/Functions".
Dies hat aber keinen Einfluss auf das vorliegende Projekt, da auf diese Funktionalitäten verzichtet werden kann.

H2 kann wie schon erwähnt direkt in die Applikation eingebunden werden, dies passiert über die Gradle Konfiguration.
```
runtime('com.h2database:h2')
```
Im Normalmodus entspricht H2 dann einer In-Memory Datenbank, was heisst, dass die Daten nach dem Neustart der Applikation verloren gehen.
Um diesem Verhalten entgegenzuwirken wird im JDBC-String explizit `file` und der Pfad zur Datei (hier im Home-Verzeichnis des Users) angegeben.
```
jdbc:h2:file:~/securityapp
```

### Spring
Spring bzw. Spring Framework ist ein Open-Source Java Framework und kann unteranderem zur Entwicklung von Webapplikation verwendet werden.

Wir verwenden zudem Spring Boot. Spring Boot macht es einfach mit der riesigen Spring Platform zu starten.
Durch sogenannte Starter können verschiedenste Abhängigkeiten zu der Spring Platform (Web, Data, etc.) auf eine einfache Weise realisiert werden.
Die Starter sind immer mit Standardkonfigurationen erstellt, damit eine Spring Boot Applikation theoretisch ohne jeweilige Konfiguration lauffähig wäre.
Dies alles führt dazu, dass Spring Boot Applikationen um einiges eifnacher zum Einrichten sind und man sich auf das wichtige konzentrieren kann. Zudem vermindert man die Konfiguration, indem man nur das nötige bzw. dort wo man vom Standard abweicht konfiguriert.

### Thymeleaf

## Datenstruktur

## Spring Security Konfiguration

## Login

## SSL

## Systemkommando