/**
 * This package contains all classes responsible for windows necessary for graphical user interface.
 * Generally exploits JFC (Swing) package.
 *
 * <p>JAK KORZYSTAĆ Z PROGRAMU PRZYCHODNIA CRUSHER 1.0</p>
 * <p>Po uruchomieniu programu wyświetla się okno logowania. Na potrzeby projektu domyślnie program nie łączy się serwerem lokalnym,
 * gdyż sprawdzenie działania programu byłoby wtedy niemożliwe,
 * lecz łączy się z serwerem "w chmurze" pod adresem 172.106.0.62 na porcie 18601. Użytkownik może zawsze zmienić adres i port klikając odpowiedni przycisk.
 * Jednak aby było to skuteczne trzeba znać hasło do JKS - na potrzeby testowania jest ono podane domyślnie. Dlatego żeby przetestować program, proponuję nie zmieniać ustawień,
 * lecz połączyć się z udostępnionym przeze mnie serwerem "w chmurze". Można zalogować się na poniższe konta:
 * <ul><li>login: <i>rejestracja</i>, hasło <i>12345678</i></li>
 * <li>login: <i>L3121126</i>, hasło <i>12345678</i></li>
 * <li>login: <i>L3121148</i>, hasło <i>12345678</i></li></ul>
 * <p>Pierwszy login posiada rolę domyślną (NO_ROLE), pozostałe dwa posiadają rolę LEKARZ.
 * W praktyce różnica między rolami sprowadza się do rożnic w dostępie do różnych struktur, szczególnie uzupełniania dokumentcaji z wizyt.</p>
 * <p>Struktura bazy danych</p>
 * <p>Baza danych składa się z tabel: Pomieszczenia, Pacjenci, Lekarze, Wizyty. Znaczenie jest intuicyjne.
 * Tabela Pomieszczenia to zbiór gabinetów do przyjmowania pacjentów, tabela Lekarze to zbiór lekarzy, tabela Pacjenci to zbiór pacjentów.
 * Tabela Pacjenci ma klucz obcy przypisujący pacjentowi lekarza prowadzącego. Tabela Wizyty to zbiór wizyt i ma trzy klucze obce, przypisujące
 * wizycie pacjenta, lekarza oraz pomieszczenie.</p>
 * <p>Jak poruszać się po programie</p>
 * <p>Klikając w różne opcje menu głównego można dodawać i wyszukiwać pacjentów, lekarzy, pomieszczenia oraz wizyty.
 * Aby zmodyfikować lub usunąć dane lekarza, pacjenta lub pomieszczenia należy w oknie wyszukiwania zaznaczyć odpowiedni rekord i klikając prawym przyciskiem otworzyć
 * menu kontekstowe które umożliwi zmianę danych lub usunięcie wpisu. Wpisy modyfikowane i usuwane są kaskadowo.
 * Realizacja wizyty możliwa jest jedynie z poziomu okna Dzisiejsze wizyty, po kliknięciu na odpowiednią pozycję prawym przyciskiem myszy.
 * Co istotne, realizować wizytę może jedynie użytkownik logujący się kontem o roli lekarza, którego identyfikator jest identyczny
 * z identyfikatorem lekarza, do którego przypisana jest wizyta.</p>
 *
 * @author Mateusz Marzec
 * @version 1.0
 * @since 2023-04-09
 */

package net.sawannaniz.databaseclient.gui;