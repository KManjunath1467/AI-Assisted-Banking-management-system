import React, { useEffect, useState } from "react";
import { fetchTransactions } from "../../api/api";
import { TransactionData } from "../../api/models";
import styles from "./TransactionsPage.module.css";

export const TransactionsPage: React.FC = () => {
    const [accountId, setAccountId] = useState<string>("1000");
    const [transactions, setTransactions] = useState<TransactionData[]>([]);
    const [filteredTransactions, setFilteredTransactions] = useState<TransactionData[]>([]);
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [filterType, setFilterType] = useState<string>("all");
    const [filterMethod, setFilterMethod] = useState<string>("all");
    const [loading, setLoading] = useState<boolean>(true);

    const loadTransactions = async (accId: string) => {
        setLoading(true);
        try {
            const data = await fetchTransactions(accId);
            setTransactions(data);
            setFilteredTransactions(data);
        } catch (e) {
            console.error("Failed to load transactions", e);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadTransactions(accountId);
    }, [accountId]);

    useEffect(() => {
        let result = [...transactions];

        if (searchQuery.trim()) {
            const q = searchQuery.toLowerCase();
            result = result.filter(
                (tx) =>
                    (tx.description && tx.description.toLowerCase().includes(q)) ||
                    (tx.recipientName && tx.recipientName.toLowerCase().includes(q)) ||
                    (tx.id && tx.id.toLowerCase().includes(q))
            );
        }

        if (filterType !== "all") {
            result = result.filter((tx) => tx.type === filterType);
        }

        if (filterMethod !== "all") {
            result = result.filter((tx) => tx.paymentType.toLowerCase() === filterMethod.toLowerCase());
        }

        setFilteredTransactions(result);
    }, [searchQuery, filterType, filterMethod, transactions]);

    const totalIncome = filteredTransactions
        .filter((tx) => tx.type === "income")
        .reduce((sum, tx) => sum + (parseFloat(tx.amount) || 0), 0);

    const totalOutcome = filteredTransactions
        .filter((tx) => tx.type === "outcome")
        .reduce((sum, tx) => sum + (parseFloat(tx.amount) || 0), 0);

    return (
        <div className={styles.container}>
            <div className={styles.headerRow}>
                <div>
                    <h1 className={styles.title}>Transaction Ledger & Statements</h1>
                    <p className={styles.subtitle}>Detailed historical ledger of all debits, credits, and electronic transfers</p>
                </div>

                <div className={styles.accountSelector}>
                    <label htmlFor="txAccSelect" className={styles.selectorLabel}>Account:</label>
                    <select
                        id="txAccSelect"
                        className={styles.selectInput}
                        value={accountId}
                        onChange={(e) => setAccountId(e.target.value)}
                    >
                        <option value="1000">Alice User (#1000)</option>
                        <option value="1010">Bob User (#1010)</option>
                        <option value="1020">Charlie User (#1020)</option>
                    </select>
                </div>
            </div>

            {/* Quick Stats Banner */}
            <div className={styles.statsGrid}>
                <div className={styles.statCard}>
                    <span className={styles.statLabel}>Total Transactions</span>
                    <strong className={styles.statValue}>{filteredTransactions.length}</strong>
                </div>
                <div className={styles.statCard}>
                    <span className={styles.statLabel}>Filtered Inflows</span>
                    <strong className={`${styles.statValue} ${styles.incomeColor}`}>+${totalIncome.toFixed(2)}</strong>
                </div>
                <div className={styles.statCard}>
                    <span className={styles.statLabel}>Filtered Outflows</span>
                    <strong className={`${styles.statValue} ${styles.outcomeColor}`}>-${totalOutcome.toFixed(2)}</strong>
                </div>
            </div>

            {/* Filter & Search Bar */}
            <div className={styles.filterBar}>
                <div className={styles.searchWrapper}>
                    <input
                        type="text"
                        placeholder="Search by description, merchant, or ID..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className={styles.searchInput}
                    />
                </div>

                <div className={styles.filterControls}>
                    <select
                        value={filterType}
                        onChange={(e) => setFilterType(e.target.value)}
                        className={styles.filterSelect}
                    >
                        <option value="all">All Flow Types</option>
                        <option value="income">Inflow (Credits)</option>
                        <option value="outcome">Outflow (Debits)</option>
                    </select>

                    <select
                        value={filterMethod}
                        onChange={(e) => setFilterMethod(e.target.value)}
                        className={styles.filterSelect}
                    >
                        <option value="all">All Payment Methods</option>
                        <option value="transfer">Bank Transfer</option>
                        <option value="creditcard">Credit Card</option>
                        <option value="directdebit">Direct Debit</option>
                    </select>

                    <button
                        onClick={() => loadTransactions(accountId)}
                        className={styles.refreshBtn}
                        title="Reload Transactions"
                    >
                        Refresh
                    </button>
                </div>
            </div>

            {/* Table */}
            {loading ? (
                <div className={styles.loadingState}>
                    <div className={styles.spinner}></div>
                    <p>Loading ledger entries...</p>
                </div>
            ) : filteredTransactions.length === 0 ? (
                <div className={styles.emptyState}>
                    <p>No transactions found matching the selected filters.</p>
                </div>
            ) : (
                <div className={styles.tableCard}>
                    <table className={styles.table}>
                        <thead>
                            <tr>
                                <th>Tx ID</th>
                                <th>Date & Time</th>
                                <th>Description / Narration</th>
                                <th>Recipient / Beneficiary</th>
                                <th>Method</th>
                                <th>Bank Code</th>
                                <th className={styles.amountCol}>Amount</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredTransactions.map((tx) => {
                                const isIncome = tx.type === "income";
                                return (
                                    <tr key={tx.id}>
                                        <td className={styles.idCell}>{tx.id}</td>
                                        <td className={styles.dateCell}>
                                            {tx.timestamp ? tx.timestamp.replace("T", " ").replace("Z", "") : "-"}
                                        </td>
                                        <td className={styles.descCell}>
                                            <span className={isIncome ? styles.inBadge : styles.outBadge}>
                                                {isIncome ? "Credit" : "Debit"}
                                            </span>
                                            {tx.description}
                                        </td>
                                        <td><strong>{tx.recipientName}</strong></td>
                                        <td><span className={styles.methodBadge}>{tx.paymentType}</span></td>
                                        <td className={styles.codeCell}>{tx.recipientBankCode || "N/A"}</td>
                                        <td className={`${styles.amountCol} ${isIncome ? styles.incomeText : styles.outcomeText}`}>
                                            {isIncome ? "+" : "-"}${Number(tx.amount).toFixed(2)}
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default TransactionsPage;
