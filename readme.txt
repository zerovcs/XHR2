Projekt testira delovanje XMLHttpRequest level2, in sicer razliène tipe responseType. 
Znano je da IE11 ne podpira "json" responseType, zato pa bi znal biti Blob zanimiv kot možnost uporabe pri downaload. 
1. Poskusiti json v èistem JS okolju - glej downloadJson.html - DELA V CHROME In FF, IE ne podpira, potrebno z JSON.parse
2. Poskusiti blob v èistem JS okolju - glej downloadBlob.html - DELA cross-platform - za IE11 obstaja specifika
3. Poskusiti json v gwt (jsinterop)
4. Poskusiti blob v gwt (jsinterop)