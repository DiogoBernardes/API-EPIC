CREATE TABLE Roles (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_name VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    removed_at TIMESTAMP
);

CREATE TABLE Users (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id UUID NOT NULL REFERENCES Roles(ID),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birthdate DATE NOT NULL,
    country VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('Active', 'Inactive')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    removed_at TIMESTAMP
);

CREATE TABLE Categories (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES Users(ID) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('Income', 'Expense')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    removed_at TIMESTAMP,
    UNIQUE (user_id, name, type)
);

CREATE TABLE Accounts (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES Users(ID) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    balance DECIMAL(18,2) DEFAULT 0.0,
    status VARCHAR(50)  NOT NULL CHECK (status IN ('Active', 'Inactive')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    removed_at TIMESTAMP,
    UNIQUE (user_id, name) 
);

CREATE TABLE Transactions (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES Users(ID) ON DELETE CASCADE,
    category_id UUID REFERENCES Categories(ID),
    account_id UUID REFERENCES Accounts(ID),
    amount DECIMAL(18,2) NOT NULL,
    description TEXT,
    transaction_date DATE NOT NULL,
    is_recurring BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    removed_at TIMESTAMP
);

CREATE TABLE Recurring_Settings (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id UUID UNIQUE NOT NULL REFERENCES Transactions(ID) ON DELETE CASCADE,
    frequency VARCHAR(20) NOT NULL CHECK (frequency IN ('Monthly', 'Yearly', 'Custom')),
    day_of_month INTEGER CHECK (day_of_month BETWEEN 1 AND 31),
    months TEXT[] NOT NULL, -- array com os meses aplicáveis, ex: '{1,2,6,12}'
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    removed_at TIMESTAMP
);

CREATE TABLE Budget (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES Users(ID) ON DELETE CASCADE,
    category_id UUID REFERENCES Categories(ID),
    amount_limit DECIMAL(18,2) NOT NULL,
    period VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    removed_at TIMESTAMP
);

CREATE TABLE Invoice_OCR (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES Users(ID) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    upload_date DATE,
    extracted_text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    removed_at TIMESTAMP
);

CREATE TABLE Reports (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES Users(ID) ON DELETE CASCADE,
    report_type VARCHAR(20) NOT NULL CHECK (report_type IN ('Monthly', 'Annual')),
    file_path VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    removed_at TIMESTAMP
);

CREATE TABLE Events (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES Users(ID) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    notify_before_days INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    removed_at TIMESTAMP
);

CREATE TABLE AI_Suggestions (
    ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES Users(ID) ON DELETE CASCADE,
    suggestion TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    removed_at TIMESTAMP
);
