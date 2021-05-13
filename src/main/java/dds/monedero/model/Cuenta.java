package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();


  public Cuenta() {
    saldo = 0;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
    this.validarMonto(cuanto);

    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    this.modificarSaldo(new Movimiento(LocalDate.now(), cuanto, false));
  }

  double montoExtraidoHoy() {
    return this.getMontoExtraidoA(LocalDate.now());
  }

  double limite() {
    return 1000 - this.montoExtraidoHoy();
  }

  public void sacar(double cuanto) {
    this.validarMonto(cuanto);
    if(getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }

    if (cuanto > this.limite()) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + this.limite());
    }
    this.modificarSaldo(new Movimiento(LocalDate.now(), cuanto, true));
  }
  public void agregarMovimiento(Movimiento movimiento) {
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

  public void validarMonto(double unMonto) {
    if(unMonto <=0)
      throw new MontoNegativoException(unMonto + ": el monto a ingresar debe ser un valor positivo");
  }

  public void modificarSaldo(Movimiento movimiento) {
    this.setSaldo(calcularValor(movimiento));
    this.agregarMovimiento(movimiento);
  }

  public double calcularValor(Movimiento movimiento) {
    if (movimiento.isDeposito()) {
      return this.getSaldo() + movimiento.getMonto();
    } else {
      return this.getSaldo() - movimiento.getMonto();
    }
  }

}
