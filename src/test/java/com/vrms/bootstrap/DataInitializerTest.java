package com.vrms.bootstrap;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.domain.Manager;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.ManagerFileRepository;
import com.vrms.persistence.VehicleFileRepository;
import org.mockito.ArgumentCaptor;
import java.util.List;
import static org.mockito.Mockito.*;

class DataInitializerTest {
    private ManagerFileRepository managerRepository;
    private VehicleFileRepository vehicleRepository;
    private DataInitializer initializer;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

    @BeforeEach
    void setUp() {
        managerRepository = mock(ManagerFileRepository.class);
        vehicleRepository = mock(VehicleFileRepository.class);
        initializer = new DataInitializer(managerRepository,vehicleRepository);
    }


	@AfterEach
	void tearDown() throws Exception {
	}

    @Test
    void initializeShouldCreateDefaultManagerAndVehiclesWhenRepositoriesAreEmpty() {
        when(managerRepository.findAll()).thenReturn(List.of());
        when(vehicleRepository.findAll()).thenReturn(List.of());

        initializer.initialize();

        ArgumentCaptor<Manager> managerCaptor = ArgumentCaptor.forClass(Manager.class);
        verify(managerRepository).save(managerCaptor.capture());

        Manager manager = managerCaptor.getValue();

        assertEquals("admin",manager.getUsername());
        assertEquals("admin123",manager.getPassword());

        ArgumentCaptor<Vehicle> vehicleCaptor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository,times(3)).save(vehicleCaptor.capture());

        List<Vehicle> vehicles = vehicleCaptor.getAllValues();

        assertEquals(3,vehicles.size());

        assertEquals("V001",vehicles.get(0).getId());
        assertEquals("Toyota Corolla",vehicles.get(0).getName());
        assertEquals("2022",vehicles.get(0).getModel());
        assertEquals(VehicleStatus.AVAILABLE,vehicles.get(0).getStatus());

        assertEquals("V002",vehicles.get(1).getId());
        assertEquals("Kia Sportage",vehicles.get(1).getName());
        assertEquals("2021",vehicles.get(1).getModel());
        assertEquals(VehicleStatus.RENTED,vehicles.get(1).getStatus());

        assertEquals("V003",vehicles.get(2).getId());
        assertEquals("Hyundai Elantra",vehicles.get(2).getName());
        assertEquals("2023",vehicles.get(2).getModel());
        assertEquals(VehicleStatus.AVAILABLE,vehicles.get(2).getStatus());
    }

    @Test
    void initializeShouldNotCreateDataWhenRepositoriesAreNotEmpty() {
        when(managerRepository.findAll()).thenReturn(List.of(new Manager("existing","1234")));
        when(vehicleRepository.findAll()).thenReturn(List.of(new Vehicle("V100","Existing Vehicle","2020",VehicleStatus.AVAILABLE)));

        initializer.initialize();

        verify(managerRepository,never()).save(any(Manager.class));
        verify(vehicleRepository,never()).save(any(Vehicle.class));
    }

    @Test
    void initializeShouldOnlyCreateManagerWhenVehicleRepositoryIsNotEmpty() {
        when(managerRepository.findAll()).thenReturn(List.of());
        when(vehicleRepository.findAll()).thenReturn(List.of(new Vehicle("V100","Existing Vehicle","2020",VehicleStatus.AVAILABLE)));

        initializer.initialize();

        verify(managerRepository).save(any(Manager.class));
        verify(vehicleRepository,never()).save(any(Vehicle.class));
    }

    @Test
    void initializeShouldOnlyCreateVehiclesWhenManagerRepositoryIsNotEmpty() {
        when(managerRepository.findAll()).thenReturn(List.of(new Manager("existing","1234")));
        when(vehicleRepository.findAll()).thenReturn(List.of());

        initializer.initialize();

        verify(managerRepository,never()).save(any(Manager.class));
        verify(vehicleRepository,times(3)).save(any(Vehicle.class));
    }

}
