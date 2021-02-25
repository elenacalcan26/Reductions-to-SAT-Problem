# Reductions-to-SAT-Problem

##### Calcan Elena-Claudia 
##### 321CA

<br/>

Task1
-------------------------------------------------------------------------------

	• sunt citite din fisier familiile mafiote si relatiile dintre acestea
	• familiile sunt privite ca pe niste noduri a unui graf, familiile care se
	inteleg fiind legate printr-o muchie
	• problema plantarii spionilor in familiile mafiote se face printr-o reducere
	la problema SAT
	• clauzele reducerii sunt scrise in metoda formulateOracleQuestion()
	• numarul de variabile folosite de catre oracol este:

			N * K, N = numarul familiilor,  K = numarul de spioni

	• variabilele folosite de catre oracol sunt date de urmatoarea relatie:

			(nr_familie * K) - j + 1 unde,

			nr_familie = numar din intervalul [1 ; N]
			K = numarul de spioni
			j = numar din intervalul [1 ; K]

			-> pentru fiecare familie X din intervalul [1 ; N], aceasta are
			urmatoarele variabile din intervalul [(X * K) - K + 1 ; (X * K)] 
	
	• numarul de clauze este dat de:

			N + K * ((K * (K - 1)) / 2 + M),

			N -> fiecare familie poate avea un spion

			K * (K * (K - 1)) / 2 -> familiile pot avea un singur spion din cei K

			K * M -> familiile prietene au spioni diferiti din cei K

	• cu un for sunt parcurse toate familiile si le sunt aplicate clauzele
	• pentru fiecare clauza este apelata cate o metoda:

			◦ spies4Families():

				‣ aplica urmatoarea clauza:

					-> fiecare familie poate avea orice spion numerotat de la 1 la K

					-> formula pentru o familie:
						 
						spion1 OR spion2 OR ...OR spionK 

					-> Complexitate caz general: O(N * K)


			◦ uniqueSpy()	
			
				‣ aplica urmatoarea clauza:

					-> fiecare familie trebuie sa aiba asignat un singur spion	

					-> formula pentru o familie:

						not spion1 OR not spion2
						.
						.
						not spion1 OR not spionK
						not spion2 OR not spion3
						.
						.
						not spion2 OR not spionK
						.
						.
						not spion(K -1) OR not spionK

					-> Complexitate caz general: O(N * K ^ 2)


			◦ differentSpies():

				‣ aplica urmatoarea clauza:

					-> fiecare familie pritena trebuie sa aiba spioni diferiti

					-> formula pentru o familie:

						not nodX_spion1 OR not nodY_spion1
						not nodX_spion2 OR not nodY_spion2
						.
						.
						not nodX_spionK OR not nodY_spionK
						not nodX_spion1 OR not nodZ_spion1
						.
						.
						not nodX_spionK OR not nodZ_spionK

					-> Complexitate O(N * nr_muchii_adiacente * K) 

	• Complexitatea metodei formulateOracleQuestion() este data de suma
	complexitatiolor celor 3 metode apelate:

		O(N * K) + O(N * K ^ 2) + O(N * nr_muchii_adiacente * K) = 
		O(N * K ^ 2) + O(N * nr_muchii_adiacente * K)

	• raspunsul oracolului este preluat in metoda decipherOracleAnswer()
	• daca raspunsul este "True" atunci, variabilele folosite sunt extrase
	• pentru fiecare varibila pozitiva se face operatia:
			variabila % K, pentru a afla spionul asignat


<br/>

Task2
-------------------------------------------------------------------------------

	• sunt citite din fisier familiile mafiote si relatiile dintre acestea
	• familiile sunt privite ca pe niste noduri a unui graf, familiile care se
	inteleg fiind legate printr-o muchie
	• problema clicii este redusa la problema SAT
	• clauzele reducerii sunt scrise in metoda formulateOracleQuestion()
	• numarul de variabile folosite de catre oracol este:

				K * N, K = dimensiunea clicii, N = numarul de familii

	• fiecare familie este reprezentat de variabile in care:

				nr_variabila % N = nr_familie,

				nr_variabila = numar din intervalul [1 ; N * K]
				N = numarul de familii
				nr_familie = numar din intervalul [1 ; N] 
		

	• numarul clauzelor este dat de:

				N + clauses.size(),

				N -> pe fiecare pozitie din clica exista un nod

				clauses.size() -> dimensiunea unui ArrayList ce retine perechi
								  de clauze


	• clauzele reducerii sunt urmatoarele:

		i) pentru fiecare i care apartine intervalului [1 ; K], exista un nod
		pe pozitia i

			-> exemplu pentru o clica de dimensiune 3 si 4 noduri:

				x_1 OR y_1 OR z_1 OR w_1 // pe pozitia 1 poate fi nodul x, y, z sau w
				x_2 OR y_2 OR z_2 OR w_2 // pe pozitia 2 poate fi nodul x, y, z sau w
				x_3 OR y_2 OR z_3 OR w_3 // pe pozitia 3 poate fi nodul x, y, z sau w

			-> clauza este scrisa folosind 2 for-uri
			-> Complexitatea pe caz general: O(N * logN)

		ii) 2 noduri care nu sunt adiacente intre ele nu pot fi amandoua in clica

				not v OR not w 	// v si w fiind variabile folosite de oracol

		iii) 2 noduri nu pot fi pe aceeasi pozitie din clica sau un nod nu 
		poate fi pe mai mult de o pozitie intr-o clica

				not v OR not w 	// v si w fiind variabile folosite de oracol

	•  nodurile carora se aplica clauzele ii) si iii) sunt salvate inainte de 
	intrebarea oracolului intr-un ArrayList ce retine perechi de literatii
	
	• aceasta actiune este facuta in metoda cliqueConditions()

		◦ sunt parcurse variabilele folosite de oracol si se extrag nodurile
		si pozitiile din clica
		◦ impartirea variabilei la N da urmatoarele:
		
				restul = nodul reprezentat de variabila
				cat + 1 = pozitia din clica a nodului respectiv

		◦ pentru clauza ii) se verifica daca cele 2 noduri nu sunt adiacente
		◦ pentru clauza iii) se verifica daca cele 2 noduri sunt egale si au 
		pozitii diferite in clica sau daca cele 2 noduri nu sunt egale si au
		aceeasi pozitie in clica

		◦ Complexitatea pe caz general: O(K * N)

	• clauzele ii) si iii) sunt scrise parcurgand ArrayList-ul cu cele 2 clauze

	• raspunsul oracolului este preluat in metoda decipherOracleAnswer()
	• daca raspunsul este "True" atunci, variabilele folosite sunt extrase
	• pentru fiecare varibila pozitiva se face operatia:
			variabila % N, pentru a afla membrii din clica

<br/>


Task3
-------------------------------------------------------------------------------

	• sunt citite din fisier familiile mafiote si relatiile dintre acestea
	• familiile sunt privite ca pe niste noduri a unui graf, familiile care se
	inteleg fiind legate printr-o muchie
	• pentru aflarea numarului minim de familii ce trebuie arestate, s-a construit
	graful complementar
	• s-a presuspus faptul ca dimensiunea maxima a clicii este N 
	• cu un for de la N pana la 2 (dimensiunea minima pe care o are o clica) este
	apelat Task2, pana cand se gaseste clica de dimensiune maxima
	• se observa o complexitate de N ori mai mare decat a task-ului 2
	• la fel ca la Task2 sunt preluati membrii cliciii
	• pentru aflarea grupului minim din graful initial sunt parcurse toate 
	nodurile si se verifica daca nodurile apar si in clica de dimensiune maxima
	a grafului complementar
	• astfel grupul minim din graful initial este compus din nodurile care nu
	apar in clica grafului complementar


<br/>

Bonus
-------------------------------------------------------------------------------

	• sunt citite din fisier familiile mafiote si relatiile dintre acestea
	• familiile sunt privite ca pe niste noduri a unui graf, familiile care se
	inteleg fiind legate printr-o muchie
	• pentru aflarea numarului minim de familii ce trebuie arestate, s-a construit
	graful complementar
	• la fel ca la Task2 problema clicii este redusa la problema SAT, avand de
	data aceasta clauze hard si soft 
	• clauzele reducerii sunt scrise in metoda formulateOracleQuestion()
	• numarul de variabile folosite de catre oracol este:

				K * N, K = dimensiunea clicii, N = numarul de familii

	• fiecare familie este reprezentat de variabile in care:

				nr_variabila % N = nr_familie,

				nr_variabila = numar din intervalul [1 ; N * K]
				N = numarul de familii
				nr_familie = numar din intervalul [1 ; N] 

	• numarul clauzelor este dat de:

				N + clauses.size(),

				N -> pe fiecare pozitie din clica exista un nod

				clauses.size() -> dimensiunea unui ArrayList ce retine perechi
								  de clauze	hard

	• ponderea clauzelor este data de:

				clauses.size() + 1,

				clauses.size() -> dimensiunea unui ArrayList ce retine perechi
								  de clauze	hard

	• clauzele reducerii sunt urmatoarele:

		◦ clauze hard: 

			i)  2 noduri care nu sunt adiacente intre ele nu pot fi amandoua in
			    clica

			ii) 2 noduri nu pot fi pe aceeasi pozitie din clica sau un nod nu 
				poate fi pe mai mult de o pozitie intr-o clica

			-> nodurile carora se aplica aceste clauze sunt salvate inainte de 
				intrebarea oracolului intr-un ArrayList ce retine perechi de 
				literatii
	
			-> aceasta actiune este facuta in metoda cliqueConditions()

			-> sunt parcurse variabilele folosite de oracol si se extrag nodurile
			   si pozitiile din clica
			
			-> impartirea variabilei la N da urmatoarele:
		
				restul = nodul reprezentat de variabila
				cat + 1 = pozitia din clica a nodului respectiv

			-> pentru clauza i) se verifica daca cele 2 noduri nu sunt adiacente
			-> pentru clauza ii) se verifica daca cele 2 noduri sunt egale si au 
			   pozitii diferite in clica sau daca cele 2 noduri nu sunt egale si au
			   aceeasi pozitie in clica

			-> Complexitatea pe caz general: O(K * N)

		
		◦ clauze soft:

			iii) pentru fiecare i care apartine intervalului [1 ; K], exista un nod
				 pe pozitia i

				 -> este o clauza soft deoarece dimensiunea clicii este aproximata
				 si pe o pozitie i nu trebuie sa existe neaparat un nod

	• aproximarea dimensiunii pe care o are clica este facut in metoda 
	cliqueDimension()

			-> se cauta nodul cu grad maxim din graful complementar deoarece
			s-a presupus faptul ca acesta o sa fie membru a clicii
			-> gradul sau este impartit la 2 deoarece nodul poate forma o
			clica de dimensiune 2 cu fiecare nod adiacent
			-> rezultatul impartirii este adunat cu 2, dimensiunea minima 
			a unei clici

	• intrebarea oracolului se face la fel ca la Task2
	• extragerea raspunsului dat de oracol si aflarea grupului minim a grafului
	initial se face ca la Task3
