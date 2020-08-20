# CityInfrastructureManager
Završni rad

Sustav City infrastructure manager ima zadaću evidentirati infrastrukturne ispade na području određenog grada, kako bi građani imali te informacije dostupne javno. 
Evidentiraju se sljedeće vrste ispada: nestanak električne energije, nestanak plina, nestanak vode, prekid prometa određeno relacijom.

Za svaki ispad se evidentira vrijeme (datum i sat, od — do) kao i dodatni slobodni tekst (npr. dio grada ili ulica na koju se ispad odnosi). 
Ispad može biti u statusu „aktivan" odnosno „završen”.Sustav naslanja se na centralnu bazu podataka. Ona se sastoji od sljedećih baza (tablica): 
baza županija, baza gradova (barem po tri grada u jednoj županiji), baza ispada i baza korisnika koji mogu evidentirati ispade. 

Gradovi i županije moraju imati evidentirane i GPS lokacije.Mobilna aplikacija omogućava pregled trenutnih ispada (današnjih ali i uz vremenski filter)
u tabelarnom obliku i na mapi, korištenje Google maps API-ja. Korisnik kod prikaza može filtrirati prikaz samo po pojedinoj županiji, 
gradu ili po vrsti ispada (npr. prikaži sve nestanke električne energije za Šibensko-kninsku županiju). Aplikacija također omogućava i tabelarni pregled 
povijesti ispada kao i prikaz najbližeg ispada, ovisno o trenutnoj lokaciji. Ovisno o vrsti ispada, neka markeri na mapi budu u drugoj boji.
Istu boju primijeniti i na tabelarni prikaz podataka.Osim programskog rješenja, 

Vaš je zadatak i kreirati bazu podataka na platformi MSSQL server,
koja će biti osnova za izradu aplikacije. Potrebno je kreirati osnovne tablice, za pojedina polja koja imaju predefinirane vrijednosti potrebno je kreirati
i šifrarnike. U osnovnim tablicama potrebno je definirati primarne ključeve i indekse. Bazu je potrebno napuniti testnim podacima.
