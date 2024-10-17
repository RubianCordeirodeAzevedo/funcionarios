import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Classe principal que executa o sistema bancário
public class Banco {
    
    // Lista para armazenar todas as contas criadas no banco
    private List<ContaBancaria> contas;

    // Construtor inicializa a lista de contas
    public Banco() {
        this.contas = new ArrayList<>();
    }

    // Método principal que roda o sistema bancário
    public static void main(String[] args) {
        Banco banco = new Banco();
        banco.menu(); // Exibe o menu principal
    }

    // Exibe o menu principal do banco
    private void menu() {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("==== Banco ====");
            System.out.println("1. Abrir Conta");
            System.out.println("2. Depositar");
            System.out.println("3. Sacar");
            System.out.println("4. Transferir");
            System.out.println("5. Consultar Saldo");
            System.out.println("6. Listar Contas");
            System.out.println("7. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    abrirConta();
                    break;
                case 2:
                    depositar();
                    break;
                case 3:
                    sacar();
                    break;
                case 4:
                    transferir();
                    break;
                case 5:
                    consultarSaldo();
                    break;
                case 6:
                    listarContas();
                    break;
                case 7:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 7);

        scanner.close();
    }

    // Método para abrir uma nova conta
    private void abrirConta() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Escolha o tipo de conta:");
        System.out.println("1. Conta Corrente");
        System.out.println("2. Conta Poupança");
        int tipoConta = scanner.nextInt();
        scanner.nextLine(); // Consumir nova linha

        System.out.print("Digite o nome do titular: ");
        String titular = scanner.nextLine();

        System.out.print("Digite o saldo inicial: ");
        double saldoInicial = scanner.nextDouble();

        ContaBancaria conta;

        if (tipoConta == 1) {
            conta = new ContaCorrente(titular, saldoInicial);
        } else if (tipoConta == 2) {
            conta = new ContaPoupanca(titular, saldoInicial);
        } else {
            System.out.println("Tipo de conta inválido.");
            return;
        }

        contas.add(conta);
        System.out.println("Conta criada com sucesso!");
        System.out.println("Número da conta: " + conta.getNumeroConta());
    }

    // Método para depositar dinheiro em uma conta
    private void depositar() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o número da conta: ");
        int numeroConta = scanner.nextInt();
        System.out.print("Digite o valor a ser depositado: ");
        double valor = scanner.nextDouble();

        ContaBancaria conta = buscarConta(numeroConta);

        if (conta != null) {
            conta.depositar(valor);
            System.out.println("Depósito realizado com sucesso.");
        } else {
            System.out.println("Conta não encontrada.");
        }
    }

    // Método para sacar dinheiro de uma conta
    private void sacar() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o número da conta: ");
        int numeroConta = scanner.nextInt();
        System.out.print("Digite o valor a ser sacado: ");
        double valor = scanner.nextDouble();

        ContaBancaria conta = buscarConta(numeroConta);

        if (conta != null) {
            try {
                conta.sacar(valor);
                System.out.println("Saque realizado com sucesso.");
            } catch (SaldoInsuficienteException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Conta não encontrada.");
        }
    }

    // Método para transferir dinheiro entre contas
    private void transferir() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o número da conta de origem: ");
        int numeroContaOrigem = scanner.nextInt();
        System.out.print("Digite o número da conta de destino: ");
        int numeroContaDestino = scanner.nextInt();
        System.out.print("Digite o valor a ser transferido: ");
        double valor = scanner.nextDouble();

        ContaBancaria contaOrigem = buscarConta(numeroContaOrigem);
        ContaBancaria contaDestino = buscarConta(numeroContaDestino);

        if (contaOrigem != null && contaDestino != null) {
            try {
                contaOrigem.transferir(contaDestino, valor);
                System.out.println("Transferência realizada com sucesso.");
            } catch (SaldoInsuficienteException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Conta de origem ou destino não encontrada.");
        }
    }

    // Método para consultar o saldo de uma conta
    private void consultarSaldo() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o número da conta: ");
        int numeroConta = scanner.nextInt();

        ContaBancaria conta = buscarConta(numeroConta);

        if (conta != null) {
            System.out.println("Saldo: R$" + conta.getSaldo());
        } else {
            System.out.println("Conta não encontrada.");
        }
    }

    // Método para listar todas as contas
    private void listarContas() {
        System.out.println("==== Lista de Contas ====");
        for (ContaBancaria conta : contas) {
            System.out.println(conta);
        }
    }

    // Método auxiliar para buscar uma conta pelo número
    private ContaBancaria buscarConta(int numeroConta) {
        for (ContaBancaria conta : contas) {
            if (conta.getNumeroConta() == numeroConta) {
                return conta;
            }
        }
        return null;
    }
}

// Classe abstrata que define uma conta bancária genérica
abstract class ContaBancaria {
    private static int geradorNumeroConta = 1000; // Gera números de conta automaticamente
    private int numeroConta;
    private String titular;
    protected double saldo;

    // Construtor inicializa a conta com titular e saldo
    public ContaBancaria(String titular, double saldoInicial) {
        this.numeroConta = geradorNumeroConta++;
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    // Métodos abstratos a serem implementados nas subclasses
    public abstract void sacar(double valor) throws SaldoInsuficienteException;

    public abstract void depositar(double valor);

    // Método para transferir dinheiro entre contas
    public void transferir(ContaBancaria contaDestino, double valor) throws SaldoInsuficienteException {
        sacar(valor); // Saca da conta de origem
        contaDestino.depositar(valor); // Deposita na conta de destino
    }

    // Getters para acessar o número da conta e saldo
    public int getNumeroConta() {
        return numeroConta;
    }

    public double getSaldo() {
        return saldo;
    }

    @Override
    public String toString() {
        return "Conta " + numeroConta + " - Titular: " + titular + " - Saldo: R$" + saldo;
    }
}

// Exceção personalizada para saldo insuficiente
class SaldoInsuficienteException extends Exception {
    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}

// Classe ContaCorrente que herda de ContaBancaria
class ContaCorrente extends ContaBancaria {

    private static final double TAXA_OPERACAO = 0.10; // Taxa de operação para saques

    // Construtor inicializa com titular e saldo
    public ContaCorrente(String titular, double saldoInicial) {
        super(titular, saldoInicial);
    }

    // Método sobrescrito para saque com taxa de operação
    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        double valorComTaxa = valor + TAXA_OPERACAO;
        if (valorComTaxa > saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente.");
        }
        saldo -= valorComTaxa;
    }

    // Método sobrescrito para depósito
    @Override
    public void depositar(double valor) {
        saldo += valor;
    }

    @Override
    public String toString() {
        return super.toString() + " (Conta Corrente)";
    }
}

// Classe ContaPoupanca que herda de ContaBancaria
class ContaPoupanca extends ContaBancaria {

    private static final double TAXA_JUROS = 0.05; // Juros aplicados ao saldo

    // Construtor inicializa com titular e saldo
    public ContaPoupanca(String titular, double saldoInicial) {
        super(titular, saldoInicial);
    }

    // Método sobrescrito para saque
    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor > saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente.");
        }
        saldo -= valor;
    }

    // Método sobrescrito para depósito
    @Override
    public void depositar(double valor) {
        saldo += valor + (valor * TAXA_JUROS);
    }

    @Override
    public String toString() {
        return super.toString() + " (Conta Poupança)";
    }
}
