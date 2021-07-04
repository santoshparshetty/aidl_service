// EMIInterface.aidl
package server_package;

// Declare any non-default types here with import statements

interface EMIInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    double calculateEMI(double principalAmount, double downPayment, double interestRate, double loanTerm);
}