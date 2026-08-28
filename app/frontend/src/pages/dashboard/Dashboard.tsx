import React, { useEffect, useState } from "react";
import { fetchAccountDetails, fetchBeneficiaries, fetchTransactions, submitPayment } from "../../api/api";
import { AccountData, Beneficiary, TransactionData } from "../../api/models";
import styles from "./Dashboard.module.css";

export const Dashboard: React.FC = () => {
    const [selectedAccountId, setSelectedAccountId] = useState<string>("1000");
    const [account, setAccount] = useState<AccountData | null>(null);
    const [beneficiaries, setBeneficiaries] = useState<Beneficiary[]>([]);
    const [transactions, setTransactions] = useState<TransactionData[]>([]);
    const [loading, setLoading] = useState<boolean>(true);

    // Transfer Modal / Form State
    const [showTransferModal, setShowTransferModal] = useState<boolean>(false);
    const [transferRecipient, setTransferRecipient] = useState<string>("");
    const [transferBankCode, setTransferBankCode] = useState<string>("0001");
    const [transferAmount, setTransferAmount] = useState<string>("");
    const [transferType, setTransferType] = useState<string>("Transfer");
    const [transferDesc, setTransferDesc] = useState<string>("");
    const [transferStatus, setTransferStatus] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

    const loadData = async (accId: string) => {
        setLoading(true);
        try {
            const [accData, benData, txData] = await Promise.all([
                fetchAccountDetails(accId),
                fetchBeneficiaries(accId),
                fetchTransactions(accId)
            ]);
            setAccount(accData);
            setBeneficiaries(benData);
            setTransactions(txData.slice(0, 5));
        } catch (e) {
            console.error("Failed to load dashboard data", e);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadData(selectedAccountId);
    }, [selectedAccountId]);

    const handleQuickTransfer = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!transferRecipient || !transferAmount || parseFloat(transferAmount) <= 0) {
            setTransferStatus("Please enter valid recipient and amount.");
            return;
        }

        setIsSubmitting(true);
        setTransferStatus(null);
        try {
            const res = await submitPayment({
                accountId: selectedAccountId,
                recipientName: transferRecipient,
                recipientBankCode: transferBankCode,
                amount: parseFloat(transferAmount).toFixed(2),
                paymentType: transferType,
                description: transferDesc || `Transfer to ${transferRecipient}`
            });
            setTransferStatus("Success: " + res.message);
            // Refresh
            await loadData(selectedAccountId);
            setTimeout(() => {
                setShowTransferModal(false);
                setTransferRecipient("");
                setTransferAmount("");
                setTransferDesc("");
                setTransferStatus(null);
            }, 1200);
        } catch (err: any) {
            setTransferStatus("Error: " + (err?.message || "Failed to process transfer"));
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className={styles.container}>
            {/* Header & Account Switcher */}
            <div className={styles.headerRow}>
                <div>
                    <h1 className={styles.title}>Financial Overview</h1>
                    <p className={styles.subtitle}>Manage your bank accounts, monitor transactions & execute transfers</p>
                </div>
                <div className={styles.accountSelector}>
                    <label htmlFor="accountSelect" className={styles.selectorLabel}>Select Active Account:</label>
                    <select
                        id="accountSelect"
                        className={styles.selectInput}
                        value={selectedAccountId}
                        onChange={(e) => setSelectedAccountId(e.target.value)}
                    >
                        <option value="1000">Alice User (USD - $5,000)</option>
                        <option value="1010">Bob User (EUR - €10,000)</option>
                        <option value="1020">Charlie User (EUR - €3,000)</option>
                    </select>
                </div>
            </div>

            {loading ? (
                <div className={styles.loadingState}>
                    <div className={styles.spinner}></div>
                    <p>Loading account metrics...</p>
                </div>
            ) : (
                <div className={styles.dashboardGrid}>
                    {/* Primary Balance Card */}
                    <div className={styles.balanceCard}>
                        <div className={styles.cardHeader}>
                            <span className={styles.cardTag}>PRIMARY ACCOUNT #{account?.id}</span>
                            <span className={styles.currencyBadge}>{account?.currency || "USD"}</span>
                        </div>
                        <div className={styles.balanceSection}>
                            <span className={styles.balanceLabel}>Available Balance</span>
                            <h2 className={styles.balanceAmount}>
                                {account?.currency === "EUR" ? "€" : "$"}
                                {account?.balance ? Number(account.balance).toLocaleString("en-US", { minimumFractionDigits: 2 }) : "0.00"}
                            </h2>
                            <p className={styles.holderName}>Account Holder: <strong>{account?.accountHolderFullName}</strong></p>
                        </div>
                        <div className={styles.cardActions}>
                            <button
                                className={styles.primaryActionBtn}
                                onClick={() => setShowTransferModal(true)}
                            >
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><line x1="12" y1="5" x2="12" y2="19"></line><polyline points="19 12 12 19 5 12"></polyline></svg>
                                Send Money / Transfer
                            </button>
                            <button
                                className={styles.secondaryActionBtn}
                                onClick={() => loadData(selectedAccountId)}
                            >
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M23 4v6h-6"></path><path d="M1 20v-6h6"></path><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path></svg>
                                Refresh Data
                            </button>
                        </div>
                    </div>

                    {/* AI Copilot Insights Card */}
                    <div className={styles.aiInsightCard}>
                        <div className={styles.aiHeader}>
                            <span className={styles.aiBadge}>✨ AI Financial Copilot</span>
                            <span className={styles.aiStatus}>Live Advisory</span>
                        </div>
                        <h3 className={styles.aiInsightTitle}>Smart Spending Summary</h3>
                        <p className={styles.aiInsightText}>
                            Your account is in excellent standing. Recurring subscriptions (Utilities, Internet) represent ~12% of monthly spend.
                            You have saved an estimated $420 compared to last quarter!
                        </p>
                        <div className={styles.quickPrompts}>
                            <span className={styles.promptChip}>"What were my recent utility bills?"</span>
                            <span className={styles.promptChip}>"Scan my latest PDF invoice"</span>
                        </div>
                    </div>

                    {/* Registered Payment Methods */}
                    <div className={styles.methodsCard}>
                        <h3 className={styles.sectionHeading}>Payment Methods & Cards</h3>
                        <div className={styles.methodsList}>
                            {account?.paymentMethods && account.paymentMethods.length > 0 ? (
                                account.paymentMethods.map((pm) => (
                                    <div key={pm.id} className={styles.methodItem}>
                                        <div className={styles.methodIcon}>
                                            💳
                                        </div>
                                        <div className={styles.methodDetails}>
                                            <strong>{pm.name}</strong>
                                            <span>Method ID: {pm.id} • Active to {pm.endDate}</span>
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <p className={styles.emptyText}>No linked cards or direct payment methods.</p>
                            )}
                        </div>
                    </div>

                    {/* Beneficiaries Card */}
                    <div className={styles.beneficiariesCard}>
                        <h3 className={styles.sectionHeading}>Registered Beneficiaries</h3>
                        <div className={styles.beneficiariesList}>
                            {beneficiaries.map((b) => (
                                <div
                                    key={b.id}
                                    className={styles.beneficiaryItem}
                                    onClick={() => {
                                        setTransferRecipient(b.name);
                                        setTransferBankCode(b.accountNumber);
                                        setShowTransferModal(true);
                                    }}
                                    title="Click to transfer to this beneficiary"
                                >
                                    <div className={styles.beneficiaryAvatar}>
                                        {b.name.charAt(0)}
                                    </div>
                                    <div className={styles.beneficiaryInfo}>
                                        <strong>{b.name}</strong>
                                        <span>{b.bankName} • Acc: {b.accountNumber}</span>
                                    </div>
                                    <button className={styles.quickSendBtn}>Send</button>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Recent Transactions Table */}
                    <div className={styles.recentTransactionsCard}>
                        <div className={styles.tableHeaderRow}>
                            <h3 className={styles.sectionHeading}>Recent Transactions</h3>
                            <a href="#/transactions" className={styles.viewAllLink}>View All History →</a>
                        </div>
                        <div className={styles.tableContainer}>
                            <table className={styles.table}>
                                <thead>
                                    <tr>
                                        <th>Description</th>
                                        <th>Recipient</th>
                                        <th>Method</th>
                                        <th>Date</th>
                                        <th className={styles.amountCol}>Amount</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {transactions.map((tx) => {
                                        const isIncome = tx.type === "income";
                                        return (
                                            <tr key={tx.id}>
                                                <td className={styles.txDesc}>
                                                    <span className={isIncome ? styles.inIcon : styles.outIcon}>
                                                        {isIncome ? "↓" : "↑"}
                                                    </span>
                                                    {tx.description}
                                                </td>
                                                <td>{tx.recipientName}</td>
                                                <td><span className={styles.typeBadge}>{tx.paymentType}</span></td>
                                                <td className={styles.dateCell}>{tx.timestamp ? tx.timestamp.split("T")[0] : "-"}</td>
                                                <td className={`${styles.amountCol} ${isIncome ? styles.incomeText : styles.outcomeText}`}>
                                                    {isIncome ? "+" : "-"}${Number(tx.amount).toFixed(2)}
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            )}

            {/* Transfer Modal */}
            {showTransferModal && (
                <div className={styles.modalBackdrop}>
                    <div className={styles.modalCard}>
                        <div className={styles.modalHeader}>
                            <h2>Initiate Funds Transfer</h2>
                            <button
                                className={styles.closeBtn}
                                onClick={() => setShowTransferModal(false)}
                            >
                                ✕
                            </button>
                        </div>
                        <form onSubmit={handleQuickTransfer} className={styles.modalForm}>
                            <div className={styles.formGroup}>
                                <label>Recipient Name</label>
                                <input
                                    type="text"
                                    required
                                    placeholder="e.g. Acme Power or John Doe"
                                    value={transferRecipient}
                                    onChange={(e) => setTransferRecipient(e.target.value)}
                                    className={styles.inputField}
                                />
                            </div>

                            <div className={styles.formRow}>
                                <div className={styles.formGroup}>
                                    <label>Amount ({account?.currency || "USD"})</label>
                                    <input
                                        type="number"
                                        step="0.01"
                                        required
                                        placeholder="0.00"
                                        value={transferAmount}
                                        onChange={(e) => setTransferAmount(e.target.value)}
                                        className={styles.inputField}
                                    />
                                </div>
                                <div className={styles.formGroup}>
                                    <label>Payment Method</label>
                                    <select
                                        value={transferType}
                                        onChange={(e) => setTransferType(e.target.value)}
                                        className={styles.inputField}
                                    >
                                        <option value="Transfer">Bank Transfer</option>
                                        <option value="CreditCard">Credit Card</option>
                                        <option value="DirectDebit">Direct Debit</option>
                                    </select>
                                </div>
                            </div>

                            <div className={styles.formGroup}>
                                <label>Memo / Description</label>
                                <input
                                    type="text"
                                    placeholder="e.g. Monthly bill settlement"
                                    value={transferDesc}
                                    onChange={(e) => setTransferDesc(e.target.value)}
                                    className={styles.inputField}
                                />
                            </div>

                            {transferStatus && (
                                <div className={`${styles.statusBanner} ${transferStatus.startsWith("Success") ? styles.statusSuccess : styles.statusError}`}>
                                    {transferStatus}
                                </div>
                            )}

                            <div className={styles.modalFooter}>
                                <button
                                    type="button"
                                    onClick={() => setShowTransferModal(false)}
                                    className={styles.cancelBtn}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    disabled={isSubmitting}
                                    className={styles.submitBtn}
                                >
                                    {isSubmitting ? "Processing..." : "Confirm & Send Funds"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Dashboard;
