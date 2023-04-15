# Desitka
- Mobilní adaptace populární vědomostní hry [Desítka](https://mindok.cz/hra/desitka/)
- Hra nabízí 2 herní režimy:
    - Hrát Online:
        - hraje se proti ostatním hráčům
        - vždy soupeří 3 hráči
    
    - Hrát s přáteli
        - 2 až 5 hráčů soupeří proti sobě
        - hráči se do hry připojí pomocí kódu, který je vygenerován při vytvoření hry
- Pro hraní je nutné internetové připojení, pokud hra ztratí spojení s hráčem, je vyřazen ze hry
- Každé kolo se hráči navzájem střídají v odpovědích, odpoví-li správně. získavají bod
- Pokud odoví hráč špatně, ztrácí všechny body získané v daném kole
- Hráč má 50 vteřin na zodpovězení otázky, pokud nezná odpověď, může se rozhodnout pasovat, tím vypadává z daného kola, ale zůstávají mu všechny body, které v něm zůstal
- Kolo končí vypadnutím všech hráčů
- Je-li hráč třikrát za sebou neaktivní (= neodpoví na otázku a ani nepasuje, je vyřazen ze hry)
- Dosáhne-li hráč 20 bodů, vyhrává hru

---

## Implementace
- Frontend je mobilní aplikace napsaná v Javě
- Backend je maven projekt napsaný v Javě

---

## Použité technologie
- Je potřeba nějaká technologie, která z notebooku vytvoří server, já jsem použil `ngrok`. Potom již stačí spustit server a potom mobilní aplikaci

### ngrok
- Více na [ngrok](https://ngrok.com/) a [stack_overflow](https://stackoverflow.com/questions/59492505/how-connect-two-pc-over-two-different-wifi-with-java-socket)
- Jedná se o bezplatnou službu, pomocí které může uživatel namapovat svůj port

### server
- Použita `IntelliJ`, lze spustit i z příkazové řádky použitím `mvn exec:java`, 

### Mobilní aplikace
- Použito `AndroidStudio`

---
## Jak to spustit
- Doporučuji zhlédnout přiložená videa 
    - [spusteni]
    - [pouziti]

- Tady je spuštění písemně:
    - vytvořit účet na [ngrok](https://ngrok.com/)
    - `choco install ngrok`
    - `ngrok tcp 4444` - namapovat port
    - přepsat namapovaný port v AndroidStudiu
    - spustit server
    - spustit aplikaci (pro spuštění na telefonu je třeba mít zapnutý vývojářský režim)
    - Už jeonom zábava