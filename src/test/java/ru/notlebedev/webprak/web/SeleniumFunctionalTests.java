package ru.notlebedev.webprak.web;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import ru.notlebedev.webprak.model.dao.DepartmentDAO;
import ru.notlebedev.webprak.model.dao.EmployeeDAO;
import ru.notlebedev.webprak.model.dao.PositionDAO;
import ru.notlebedev.webprak.model.dao.PositionHistoryDAO;
import ru.notlebedev.webprak.model.entity.Department;
import ru.notlebedev.webprak.model.entity.Employee;
import ru.notlebedev.webprak.model.entity.Position;
import ru.notlebedev.webprak.model.entity.PositionHistoryEntry;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:application.properties")
public class SeleniumFunctionalTests {
    @LocalServerPort
    private int port;
    @Autowired
    private WebDriver driver;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private DepartmentDAO departmentDAO;
    @Autowired
    private PositionDAO positionDAO;
    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private PositionHistoryDAO positionHistoryDAO;

    @Test
    public void testEmpFind() {
        driver.get("localhost:" + port + "/");
        driver.findElement(By.linkText("Список служащих")).click();
        waitUntilLoads();
        driver.findElement(By.name("name")).click();
        driver.findElement(By.name("name")).sendKeys("Попов");
        driver.findElement(By.xpath("//*[contains(text(), 'Подразделение')]")).click();
        driver.findElement(By.xpath("//*[contains(text(), 'Заготовки')]")).click();
        driver.findElement(By.xpath("//input[@type='submit' and @value='Применить']")).click();
        waitUntilLoads();
        List<WebElement> elements = driver.findElements(By
                .xpath("//table[@class='autoTable']/tbody/tr//a"));
        assertEquals(1, elements.size());
        assertEquals("Попов Сергей Дмитриевич", elements.get(0).getText());
    }

    @Test
    public void testEmpFindFail() {
        driver.get("localhost:" + port + "/");
        driver.findElement(By.linkText("Список служащих")).click();
        waitUntilLoads();
        driver.findElement(By.name("name")).click();
        driver.findElement(By.name("name")).sendKeys("Безымяненко");
        driver.findElement(By.xpath("//*[contains(text(), 'Подразделение')]")).click();
        driver.findElement(By.xpath("//*[contains(text(), 'Заготовки')]")).click();
        driver.findElement(By.xpath("//input[@type='submit' and @value='Применить']")).click();
        waitUntilLoads();
        List<WebElement> elements = driver.findElements(By
                .xpath("//table[@class='autoTable']/tbody/tr/td[@colspan='3']"));
        assertEquals(1, elements.size());
        assertEquals("Служащих по заданному фильтру не найдено.", elements.get(0).getText());
    }

    @Test
    public void testEmpHistory() {
        driver.get("localhost:" + port + "/");
        driver.findElement(By.linkText("Список служащих")).click();
        waitUntilLoads();
        driver.findElement(By.name("name")).click();
        driver.findElement(By.name("name")).sendKeys("Прасковья Аркадьевна");
        driver.findElement(By.xpath("//input[@type='submit' and @value='Применить']")).click();
        waitUntilLoads();
        List<WebElement> elements = driver.findElements(By
                .xpath("//table[@class='autoTable']/tbody/tr//a"));
        assertEquals(1, elements.size());
        assertEquals("Прасковья Аркадьевна", elements.get(0).getText());
        elements.get(0).click();
        waitUntilLoads();
        driver.findElement(By.xpath("//a[contains(text(), 'История занимаемых должностей')]")).click();
        waitUntilLoads();
        elements = driver.findElements(By
                .xpath("//table[@class='autoTable']/tbody/tr"));
        assertEquals(4, elements.size());

        List<String> expected = List.of("Бухгалтер", "Старший бухгалтер");
        for (int i = 0; i < 2; i++) {
            WebElement element = elements.get(i);
            assertEquals("Бухгалтерия", element.findElement(By.xpath(".//a")).getText());
            String expectedPosition = expected.get(i);
            assertEquals(expectedPosition, element.findElements(By.xpath("./td")).get(1).getText());
        }
    }

    private void waitUntilLoads() {
        new WebDriverWait(driver, 1).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }

    @BeforeEach
    public void fillDB() {
        Department dep0 = new Department("ООО Рога и Копыта", Department.Status.ACTIVE);
        Department dep1 = new Department("Бухгалтерия", Department.Status.ACTIVE);
        Department dep2 = new Department("Заготовки", Department.Status.ACTIVE);
        Department dep3 = new Department("Заготовка хвостов", Department.Status.ACTIVE);

        dep1.setDepartmentSuper(dep0);
        dep2.setDepartmentSuper(dep0);
        dep3.setDepartmentSuper(dep3);
        departmentDAO.save(dep0);
        departmentDAO.save(dep1);
        departmentDAO.save(dep2);
        departmentDAO.save(dep3);

        Position pos0 = new Position(dep0, "Директор", "", Position.Status.ACTIVE);
        Position pos1 = new Position(dep1, "Старший бухгалтер", "", Position.Status.ACTIVE);
        Position pos2 = new Position(dep1, "Бухгалтер", "", Position.Status.ACTIVE);
        Position pos3 = new Position(dep2, "Старший заготовщик", "", Position.Status.ACTIVE);
        Position pos4 = new Position(dep3, "Старший заготовщик хвостов", "", Position.Status.ACTIVE);
        Position pos5 = new Position(dep3, "Заготовщик хвостов", "", Position.Status.ACTIVE);
        positionDAO.save(pos0);
        positionDAO.save(pos1);
        positionDAO.save(pos2);
        positionDAO.save(pos3);
        positionDAO.save(pos4);
        positionDAO.save(pos5);

        Employee emp0 = new Employee("Остап Ибрагимович", "ул. Пушкина, дом 12, квартира 5",
                "Высшее", "Санкт-Петербургский политехнический университет Петра Великого");
        Employee emp1 = new Employee("Прасковья Аркадьевна", "ул. Егорьевский проезд, дом 98, квартира 532",
                "Высшее", "Томский Финансовый Университет");
        Employee emp2 = new Employee("Пётр Сергеевич", "ул. Ленина, дом 13, квартира 3",
                "Высшее", "Московский Финансовый Университет");
        Employee emp3 = new Employee("Попов Сергей Дмитриевич", "Ленинский пр., д. 14 к.1",
                "Среднее", "Московский технологический колледж");

        employeeDAO.save(emp0);
        employeeDAO.save(emp1);
        employeeDAO.save(emp2);
        employeeDAO.save(emp3);

        PositionHistoryEntry entr0 = new PositionHistoryEntry(pos0, emp0, PositionHistoryEntry.Status.ACTIVE,
                Date.valueOf("2022-01-01"));
        PositionHistoryEntry entr1 = new PositionHistoryEntry(pos2, emp1, PositionHistoryEntry.Status.FINISHED,
                Date.valueOf("2022-01-01"));
        entr1.setDateEnd(Date.valueOf("2022-02-01"));
        PositionHistoryEntry entr2 = new PositionHistoryEntry(pos1, emp1, PositionHistoryEntry.Status.ACTIVE,
                Date.valueOf("2022-02-01"));
        PositionHistoryEntry entr3 = new PositionHistoryEntry(pos2, emp2, PositionHistoryEntry.Status.ACTIVE,
                Date.valueOf("2022-02-01"));
        PositionHistoryEntry entr4 = new PositionHistoryEntry(pos3, emp3, PositionHistoryEntry.Status.ACTIVE,
                Date.valueOf("2022-01-01"));

        positionHistoryDAO.save(entr0);
        positionHistoryDAO.save(entr1);
        positionHistoryDAO.save(entr2);
        positionHistoryDAO.save(entr3);
        positionHistoryDAO.save(entr4);
    }

    @AfterEach
    void cleanDB() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createSQLQuery("TRUNCATE departments RESTART IDENTITY CASCADE;").executeUpdate();
            session.createSQLQuery("TRUNCATE employees RESTART IDENTITY CASCADE;").executeUpdate();
            session.createSQLQuery("TRUNCATE position_history RESTART IDENTITY CASCADE;").executeUpdate();
            session.createSQLQuery("TRUNCATE positions RESTART IDENTITY CASCADE;").executeUpdate();
            session.createSQLQuery("ALTER SEQUENCE hibernate_sequence RESTART WITH 1;").executeUpdate();
            session.getTransaction().commit();
        }
    }
}
