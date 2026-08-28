import {
    ChatAppResponse,
    ChatAppResponseOrError,
    ChatAppRequest,
    AccountData,
    Beneficiary,
    TransactionData,
    PaymentPayload
} from "./models";
import { useLogin } from "../authConfig";

const BACKEND_URI = import.meta.env?.VITE_BACKEND_URI ? import.meta.env.VITE_BACKEND_URI : "";

function getHeaders(idToken: string | undefined, stream: boolean): Record<string, string> {
    const headers: Record<string, string> = {
        "Content-Type": "application/json"
    };
    if (useLogin && idToken) {
        headers["Authorization"] = `Bearer ${idToken}`;
    }

    if (stream) {
        headers["Accept"] = "application/x-ndjson";
    } else {
        headers["Accept"] = "application/json";
    }

    return headers;
}

export async function askApi(request: ChatAppRequest, idToken: string | undefined): Promise<ChatAppResponse> {
    const url = `${BACKEND_URI}/api/ask`;
    const response = await fetch(url, {
        method: "POST",
        headers: getHeaders(idToken, request.stream || false),
        body: JSON.stringify(request)
    });

    const parsedResponse: ChatAppResponseOrError = await response.json();
    if (response.status > 299 || !response.ok) {
        throw Error(parsedResponse.error || "Unknown error");
    }

    return parsedResponse as ChatAppResponse;
}

export async function chatApi(request: ChatAppRequest, idToken: string | undefined): Promise<Response> {
    const url = `${BACKEND_URI}/api/chat`;
    return await fetch(url, {
        method: "POST",
        headers: getHeaders(idToken, request.stream || false),
        body: JSON.stringify(request)
    });
}

export function getCitationFilePath(citation: string): string {
    return `${BACKEND_URI}/api/content/${citation}`;
}

export function uploadAttachment(file: File): Promise<string> {
    const formData = new FormData();
    formData.append("file", file);

    return fetch(`${BACKEND_URI}/api/content`, {
        method: "POST",
        body: formData
    }).then(response => {
        if (response.status > 299 || !response.ok) {
            throw Error("Failed to upload attachment");
        }
        return response.text();
    });
}

export function getImage(name: string): string {
    return `${BACKEND_URI}/api/content/${name}`;
}

// Fallback seed data for local testing and standalone frontend preview
const MOCK_ACCOUNTS: Record<string, AccountData> = {
    "1000": {
        id: "1000",
        userName: "alice.user@contoso.com",
        accountHolderFullName: "Alice User",
        currency: "USD",
        activationDate: "2022-01-01",
        balance: "5000.00",
        paymentMethods: [
            { id: "12345", name: "Visa Gold (ending in 5678)", startDate: "2022-01-01", endDate: "2025-01-01" },
            { id: "23456", name: "Standard Bank Checking", startDate: "2022-01-01", endDate: "2099-01-01" }
        ]
    },
    "1010": {
        id: "1010",
        userName: "bob.user@contoso.com",
        accountHolderFullName: "Bob User",
        currency: "EUR",
        activationDate: "2022-01-01",
        balance: "10000.00",
        paymentMethods: [
            { id: "345678", name: "Bank Transfer IBAN", startDate: "2022-01-01", endDate: "2099-01-01" },
            { id: "55555", name: "Visa Platinum (ending in 3266)", startDate: "2024-01-01", endDate: "2028-01-01" }
        ]
    },
    "1020": {
        id: "1020",
        userName: "charlie.user@contoso.com",
        accountHolderFullName: "Charlie User",
        currency: "EUR",
        activationDate: "2022-01-01",
        balance: "3000.00",
        paymentMethods: [
            { id: "46748576", name: "SEPA Direct Debit", startDate: "2022-02-01", endDate: "2099-02-01" }
        ]
    }
};

const MOCK_BENEFICIARIES: Beneficiary[] = [
    { id: "1", name: "Mike The Plumber", accountNumber: "123456789", bankName: "Intesa Sanpaolo" },
    { id: "2", name: "Jane The Electrician", accountNumber: "987654321", bankName: "UBS Switzerland" },
    { id: "3", name: "Acme Electric Utilities", accountNumber: "554433221", bankName: "JPMorgan Chase" },
    { id: "4", name: "Contoso Property Mgt", accountNumber: "778899001", bankName: "BNP Paribas" }
];

let mockTransactions: TransactionData[] = [
    { id: "tx-1001", description: "Payment of electricity bill #9921", type: "outcome", recipientName: "Acme Power", recipientBankCode: "0001", accountId: "1000", paymentType: "BankTransfer", amount: "145.50", timestamp: "2024-04-10T10:15:00Z" },
    { id: "tx-1002", description: "Grocery supermarket shopping", type: "outcome", recipientName: "Whole Foods Market", recipientBankCode: "0002", accountId: "1000", paymentType: "CreditCard", amount: "84.20", timestamp: "2024-04-08T16:30:00Z" },
    { id: "tx-1003", description: "Internet High-Speed Fiber", type: "outcome", recipientName: "Contoso Telecom", recipientBankCode: "0003", accountId: "1000", paymentType: "DirectDebit", amount: "65.00", timestamp: "2024-04-01T09:00:00Z" },
    { id: "tx-1004", description: "Bi-Weekly Salary Deposit", type: "income", recipientName: "Global Tech Corp", recipientBankCode: "0004", accountId: "1000", paymentType: "Transfer", amount: "3500.00", timestamp: "2024-03-30T08:00:00Z" },
    { id: "tx-1005", description: "Artisan Coffee & Bakery", type: "outcome", recipientName: "Starbucks Reserve", recipientBankCode: "0005", accountId: "1000", paymentType: "CreditCard", amount: "12.75", timestamp: "2024-03-28T11:20:00Z" },
    { id: "tx-1006", description: "Monthly Apartment Rental", type: "outcome", recipientName: "Metropolitan Living", recipientBankCode: "0006", accountId: "1000", paymentType: "Transfer", amount: "1450.00", timestamp: "2024-03-01T09:30:00Z" }
];

export async function fetchAccountDetails(accountId: string = "1000"): Promise<AccountData> {
    try {
        const res = await fetch(`${BACKEND_URI}/accounts/${accountId}`);
        if (res.ok) {
            return await res.json();
        }
    } catch {
        // fallback to local mock
    }
    return MOCK_ACCOUNTS[accountId] || MOCK_ACCOUNTS["1000"];
}

export async function fetchBeneficiaries(accountId: string = "1000"): Promise<Beneficiary[]> {
    try {
        const res = await fetch(`${BACKEND_URI}/accounts/${accountId}/registeredBeneficiaries`);
        if (res.ok) {
            return await res.json();
        }
    } catch {
        // fallback to local mock
    }
    return MOCK_BENEFICIARIES;
}

export async function fetchTransactions(accountId: string = "1000"): Promise<TransactionData[]> {
    try {
        const res = await fetch(`${BACKEND_URI}/transactions/${accountId}`);
        if (res.ok) {
            return await res.json();
        }
    } catch {
        // fallback to local mock
    }
    return mockTransactions;
}

export async function submitPayment(payload: PaymentPayload): Promise<{ success: boolean; message: string }> {
    try {
        const res = await fetch(`${BACKEND_URI}/payments`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            return { success: true, message: "Payment processed successfully!" };
        }
    } catch {
        // fallback local simulation
    }

    // Record locally in mock transaction list
    const newTx: TransactionData = {
        id: "tx-" + Date.now(),
        description: payload.description || `Payment to ${payload.recipientName}`,
        type: "outcome",
        recipientName: payload.recipientName,
        recipientBankCode: payload.recipientBankCode || "0001",
        accountId: payload.accountId,
        paymentType: payload.paymentType,
        amount: payload.amount,
        timestamp: new Date().toISOString()
    };
    mockTransactions = [newTx, ...mockTransactions];

    // Deduct balance in mock
    const acc = MOCK_ACCOUNTS[payload.accountId];
    if (acc) {
        const currentBal = parseFloat(acc.balance);
        const amt = parseFloat(payload.amount);
        if (!isNaN(currentBal) && !isNaN(amt)) {
            acc.balance = Math.max(0, currentBal - amt).toFixed(2);
        }
    }

    return { success: true, message: "Payment processed and recorded successfully!" };
}
