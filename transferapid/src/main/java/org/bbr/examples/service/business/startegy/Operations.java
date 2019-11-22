package org.bbr.examples.service.business.startegy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.bbr.examples.service.business.model.account.Accounts;
import org.bbr.examples.service.business.model.customer.CustomerAccount;
import org.bbr.examples.service.business.model.customer.Customers;
import org.bbr.examples.service.business.model.transfer.TransferMethod;
import org.bbr.examples.service.business.model.transfer.Transfers;

/**
 * Some supported transfer operations.
 * For example a Local transfer between two Card accounts of two Persons - in this case a Basic strategy will be applied.
 */
public class Operations {

	private static final String KEY_FORMAT = "%1$s|%2$s|%3$s|%4$s|%5$s";

	private static String key(Customers sender, Accounts senderAccount, Customers recipient, Accounts recipientAccount,
			Transfers transfer) {
		return String.format(KEY_FORMAT, sender, senderAccount, recipient, recipientAccount, transfer);
	}

	private static final Map<String, Supplier<Strategy>> available;

	static {
		available = new HashMap<>();
		available.put(key(Customers.Person, Accounts.Card, Customers.Person, Accounts.Card, Transfers.Local),
				Basic::new);
		available.put(key(Customers.Person, Accounts.Card, Customers.Person, Accounts.Bank, Transfers.Local),
				Basic::new);
		available.put(key(Customers.Person, Accounts.Bank, Customers.Person, Accounts.Bank, Transfers.Foreign),
				Extra::new);
		available.put(key(Customers.Person, Accounts.Bank, Customers.Corporate, Accounts.Bank, Transfers.Special),
				Extra::new);
		available.put(key(Customers.Person, Accounts.Crypto, Customers.Person, Accounts.Crypto, Transfers.Foreign),
				Basic::new);
		available.put(key(Customers.Corporate, Accounts.Bank, Customers.Person, Accounts.Bank, Transfers.Local),
				Business::new);
		available.put(key(Customers.Corporate, Accounts.Bank, Customers.Corporate, Accounts.Bank, Transfers.Local),
				Business::new);
		available.put(key(Customers.Corporate, Accounts.Bank, Customers.Corporate, Accounts.Bank, Transfers.Foreign),
				Business::new);
	}

	public static Operations instance() {
		return new Operations();
	}

	public Strategy chooseStrategy(CustomerAccount sender, CustomerAccount recipient, TransferMethod transfer) {
		return available.computeIfAbsent(key(sender.getCustomerType(), sender.getAccountType(),
				recipient.getCustomerType(), recipient.getAccountType(), transfer.getType()), (k) -> Unsupported::new)
				.get();
	}

}
