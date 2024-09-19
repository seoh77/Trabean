CREATE SCHEMA IF NOT EXISTS TRABEAN;

USE TRABEAN;

CREATE TABLE `users` (
    `user_id` bigint NOT NULL,
    `user_key` VARCHAR(40) UNIQUE NOT NULL,
    `email` VARCHAR(100) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,  -- 암호는 보통 더 긴 해시값을 저장하므로 길이를 늘림
    `name` VARCHAR(10) NOT NULL,
    `payment_account_id` bigint NULL,
    `main_account_id` bigint UNIQUE NULL,
    PRIMARY KEY (`user_id`)
);

CREATE TABLE `accounts` (
    `account_id` bigint NOT NULL,
    `account_no` VARCHAR(16) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,  -- 암호 길이 늘림
    `user_id` bigint NOT NULL,
    PRIMARY KEY (`account_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) -- user_id와 연관된 외래 키
);

CREATE TABLE `user_accounts` (
    `user_account_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `account_id` bigint NOT NULL,
    `role` enum('admin', 'payer', 'nonePayer') NOT NULL,
    PRIMARY KEY (`user_account_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`), -- users와 연관된 외래 키
    FOREIGN KEY (`account_id`) REFERENCES `accounts`(`account_id`) -- accounts와 연관된 외래 키
);

CREATE TABLE `travel_accounts` (
    `account_id` bigint NOT NULL,
    `account_name` VARCHAR(20) NOT NULL,
    `exchange_currency` VARCHAR(5) NOT NULL,
    `parent_account_id` bigint NOT NULL,
    `target_amount` bigint NOT NULL,
    PRIMARY KEY (`account_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts`(`account_id`) -- accounts와 연관된 외래 키
);

CREATE TABLE `merchants` (
    `merchant_id` bigint NOT NULL,
    `account_no` VARCHAR(16) NOT NULL,
    `name` VARCHAR(20) NOT NULL,
    `category` ENUM('transportation', 'food', 'shopping', 'accommodation', 'activity', 'other') NOT NULL,
    `exchange_currency` VARCHAR(5) NOT NULL,
    PRIMARY KEY (`merchant_id`)
);

CREATE TABLE `payments` (
    `pay_id` bigint NOT NULL,
    `account_id` bigint NULL,
    `merchant_id` bigint NULL,
    `transaction_id` VARCHAR(100) UNIQUE NOT NULL,
    `payment_date` Timestamp NULL,
    `krw_amount` bigint NULL,
    `foreign_amount` double NULL,
    `payment_status` enum('success', 'cancel', 'passwordError', 'balanceError', 'pending') NOT NULL DEFAULT 'pending',
    `password_error_count` int NOT NULL DEFAULT 0,
    PRIMARY KEY (`pay_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts`(`account_id`), -- accounts와 연관된 외래 키
    FOREIGN KEY (`merchant_id`) REFERENCES `merchants`(`merchant_id`) -- merchants와 연관된 외래 키
);

CREATE TABLE `invitations` (
    `invitation_id` bigint NOT NULL,
    `email` VARCHAR(100) NOT NULL,
    `is_accepted` boolean NOT NULL DEFAULT false,
    `account_id` bigint NOT NULL,
    `invite_date` Timestamp NOT NULL,
    PRIMARY KEY (`invitation_id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts`(`account_id`) -- accounts와 연관된 외래 키
);

CREATE TABLE `notifications` (
    `notification_id` bigint NOT NULL,
    `sender_id` bigint NOT NULL,
    `receiver_id` bigint NOT NULL,
    `type` enum('invite', 'deposit', 'withdraw', 'payment') NOT NULL,
    `create_time` timestamp NOT NULL,
    `is_read` boolean NOT NULL DEFAULT false,
    `amount` bigint NULL,
    `account_id` bigint NOT NULL,
    PRIMARY KEY (`notification_id`),
    FOREIGN KEY (`sender_id`) REFERENCES `users`(`user_id`), -- users와 연관된 외래 키
    FOREIGN KEY (`receiver_id`) REFERENCES `users`(`user_id`), -- users와 연관된 외래 키
    FOREIGN KEY (`account_id`) REFERENCES `accounts`(`account_id`) -- accounts와 연관된 외래 키
);

CREATE TABLE `payout` (
    `payout_id` bigint NOT NULL,
    `merchant_id` bigint NOT NULL,
    `status` enum('success', 'failed', 'pending') NOT NULL DEFAULT 'pending',
    PRIMARY KEY (`payout_id`),
    FOREIGN KEY (`merchant_id`) REFERENCES `merchants`(`merchant_id`) -- merchants와 연관된 외래 키
);
