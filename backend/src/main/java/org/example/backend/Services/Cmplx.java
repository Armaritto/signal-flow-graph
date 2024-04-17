package org.example.backend.Services;

public class Cmplx {
    private double real;
    private double imaginary;

    public double getReal() {
        return real;
    }

    public void setReal(double real) {
        this.real = real;
    }

    public double getImaginary() {
        return imaginary;
    }

    public void setImaginary(double imaginary) {
        this.imaginary = imaginary;
    }

    public Cmplx(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }
}
