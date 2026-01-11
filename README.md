# Projects Service

## Opis mikrostoritve
**Projects Service** je ena izmed mikrostoritev v spletni aplikaciji **Lab Manager**.
Skrbi za upravljanje projektov, ki se izvajajo v okviru laboratorijev, ter za shranjevanje vseh ključnih podatkov, povezanih s projekti. To vključuje osnovne informacije o projektu, časovno obdobje izvajanja, odgovorne uporabnike ter povezave do uporabljenih laboratorijev in opreme.

Ob ustvaritvi novega projekta se mora ustrezno rezervirati oprema, ki jo sodelujoči na projektu potrebujejo. V ta namen, mikrostoritev sodeluje z **Labs-service**. Ko uporabnik kreira projekt, ter izbere opremo in laboratorij, se ta dva podatka posredujeta v Projects service, kjer se v okiru transakcije najprej pošlje zahteva za rezervacijo v Labs Service, nato pa se ustvari nov projekt in zabeleži tudi njegovo opremo. Lab client je implementiran v LabServiceClient.java.

Mikrostoritev podpira tudi funkcionalnost generiranja opreme iz opisa projekta. V ta namen je integriran zunanji **Gemini API**, katerega komunikacija je implementirana v EquipmentSuggestionService.java.

Storitev je implementirana kot **SpringBoot REST API**. API je implementiran v datoteki ProjectsController.java.

## Funkcionalnosti

**ProjectsService.java:**
- Ustvarjanje projekta
- Pridobivanje podatkov laboratorija
- Filtriranje projektov glede na status/ ID uporabnika/ ID laboratorija
- Izbris projekta

**EquipmentSuggestionService.java:**  
Prejme opis projekta in seznam vseh pripomočkov, ki jih ima na voljo. Sestavi prompt, ki ga pošlje na Gemini API, ter iz njega ekstrahira odgovor. Vrne pripomočke in njihovo količino v obliki JSON (`List<Equipment Request>`).

## Swagger API dokumentacija
Za podroben opis končnih točk mikrostoritve (formati zahtev/odgovorov, vračanje napak), ob zagonu mikrostoritve obiščite: http://localhost:8082/swagger-ui/index.html

## Entitete
- **`Project`** (v `projects-service`): Projekt v laboratoriju; polja: `id` (PK), `name`, `labId`, `description`, `startDate`, `endDate`, `projectLeader` (userId), `status` (enum: ACTIVE/COMPLETED/CANCELED), `participants` (seznam userId-jev) in `equipment` (seznam `ProjectEquipment`). Upravlja življenjski cikel in članstvo projekta.

- **`ProjectEquipment`** (v `projects-service`): Element opreme povezan s projektom; polja: `id` (PK), `project` (ManyToOne povezava), `name` in `usedQuantity`. Pove, katera oprema in v kakšnem obsegu je dodeljena projektu.


## DTO
Kratek pregled DTO (Data Transfer Objects) v **projects-service**:

- **`EquipmentRequest`**: preprost DTO z `name` in `stock` — uporabljen za zahteve rezervacije/opreme (npr. pri ustvarjanju projekta ali rezervaciji v labs-service).
- **`CreateProjectRequest`**: ovijalec, ki vsebuje `project` (entiteta `Project`) in `equipmentRequests` (`List<EquipmentRequest>`). Uporablja se pri API klicu za ustvarjanje projekta.
- **`EquipmentGenerationRequest`**: vsebuje `description` (opis projekta) in `availableEquipment` (`List<String>`); uporablja se kot vhod za EquipmentSuggestionService, ki predlaga opremo (npr. `EquipmentSuggestionService`).

## Navodila za namestitev
**Predpogoji:**
- Java 17
- Maven

**Namestitev**
```bash
# lokalna namestitev repozitorija
cd labManager
git clone https://github.com/LabManager-app/projects-service.git
cd projects-service
```
**Zagon**
```bash
mvn clean package
mvn spring-boot:run

# ali (v primeru uporabe Docker)
docker compose up --build projects-service
```
Mikrostoritev bo dostopna na http://localhost:8082  

### Gemini API 
V kolikor želite preizkusiti funkcionalnost samodejnega generiranja opreme, je potrebno pridobiti svoj Gemini API ključ:  
1. Obišči Google AI studio: https://aistudio.google.com/api-keys.
2. Izberi *Create API key*, nastaviš poljubno ime.
3. Po ustvaritvi kopiraš token.
4. Nastaviš ključ preko okoljske spremenljivke:
```
# Linux:
export GEMINI_API_KEY="tvoj_geslo_ali_token"

# Windows:
set GEMINI_API_KEY=tvoj_token
```

