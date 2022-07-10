# WebServer
Studenten Projekt mit einem simplen webserver und integrierter RESTapi
## @autoren Gerasimos Strecker and Konstantin Regenhardt
- Unsere Arbeit kann man schön Visualisiert in der Datei GitHub.mp4 sehen
- *Als client sowie für Testzwecke wurde "Postman" benutzt, owbohl es auch mit CURL funktionieren sollte*

Funktionsweise (Erklärt an Postman):
  - Zum starten des Webservers muss die Datei "Webserver.java" gestartet werden (Dateipfad: \WebServer\server\Webserver.java)
      - Nun steht in der Konsole die IP Adresse und Localport welche bei Postman eingegeben werden müssen
  - Für GET bei Postman dieses Format benutzen: IP+Port+Pfad z.B.: 192.168.2.35:8080\website\index.html
    - Verfügbare Pfade sind:
      - \website\index.html
      - \files\file<nr>.txt
  - Für POST bei Postman dieses Format benutzen: IP+Port
    - Wenn eine Json Datei geschickt werden soll muss diese in den Body gespeichert werden im Format *raw*
    - Wenn eigene Json benutzt werden soll muss eine Leere Zeile am Ende sein
    - Benutztes Beispiel von uns ist in der Datei **BeispielJson.txt**
