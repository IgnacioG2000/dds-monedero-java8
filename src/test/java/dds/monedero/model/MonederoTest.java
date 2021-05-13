package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void Poner() {
    cuenta.poner(1500);
    assertEquals(cuenta.getSaldo(),1500);
    assertFalse(cuenta.getMovimientos().isEmpty());
  }


  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void RealizarUnDepositoValido() {
    cuenta.poner(500);
    cuenta.poner(965);
    assertEquals(cuenta.getSaldo(),1465);
    assertEquals(cuenta.getMovimientos().size(),2);
  }

  @Test
  void TresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    assertEquals(cuenta.getSaldo(),3856);
    assertEquals(cuenta.getMovimientos().size(),3);
  }

  @Test
  void EsInvalidoRealizarMasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
    });
  }

  @Test
  void RealizarUnaExtraccionValida() {
    cuenta.setSaldo(500);
    cuenta.sacar(50);
    assertEquals(cuenta.getSaldo(),450);
    assertFalse(cuenta.getMovimientos().isEmpty());
  }

  @Test
  void EsInvalidoExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.sacar(1001);
    });
  }

  @Test
  public void EsInvalidoExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void EsInvalidoExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

}