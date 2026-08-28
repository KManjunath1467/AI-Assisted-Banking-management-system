import { Outlet, NavLink, Link } from "react-router-dom";
import styles from "./Layout.module.css";
import { useLogin } from "../../authConfig";
import { LoginButton } from "../../components/LoginButton";

export const Layout = () => {
    return (
        <div className={styles.layout}>
            <header className={styles.header} role="banner">
                <div className={styles.headerContainer}>
                    <Link to="/" className={styles.headerTitleContainer}>
                        <div className={styles.logoIcon}>🏦</div>
                        <div>
                            <h3 className={styles.headerTitle}>NovaBank Copilot</h3>
                            <span className={styles.headerSubtitle}>AI Intelligent Financial Assistant</span>
                        </div>
                    </Link>

                    <nav className={styles.navMenu}>
                        <ul className={styles.headerNavList}>
                            <li>
                                <NavLink
                                    to="/"
                                    end
                                    className={({ isActive }) =>
                                        isActive ? styles.headerNavPageLinkActive : styles.headerNavPageLink
                                    }
                                >
                                    💬 AI Copilot
                                </NavLink>
                            </li>
                            <li>
                                <NavLink
                                    to="/dashboard"
                                    className={({ isActive }) =>
                                        isActive ? styles.headerNavPageLinkActive : styles.headerNavPageLink
                                    }
                                >
                                    📊 Dashboard & Accounts
                                </NavLink>
                            </li>
                            <li>
                                <NavLink
                                    to="/transactions"
                                    className={({ isActive }) =>
                                        isActive ? styles.headerNavPageLinkActive : styles.headerNavPageLink
                                    }
                                >
                                    📜 Transaction Ledger
                                </NavLink>
                            </li>
                        </ul>
                    </nav>

                    <div className={styles.rightSection}>
                        <div className={styles.activeSystemBadge}>
                            <span className={styles.pulseDot}></span>
                            <span>Banking Services Online</span>
                        </div>
                        {useLogin && <LoginButton />}
                    </div>
                </div>
            </header>

            <main className={styles.content}>
                <Outlet />
            </main>
        </div>
    );
};

export default Layout;
