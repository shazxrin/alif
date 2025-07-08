import pandas as pd
from pandas.core.interchange.dataframe_protocol import DataFrame


def process_am_time(string: str) -> str:
    if pd.isna(string):
        return string
    parts = string.strip().split(" ")
    hour = int(parts[0])
    minute = int(parts[1])
    return f"{hour:02d}:{minute:02d}"


def process_pm_time(string: str) -> str:
    if pd.isna(string):
        return string
    parts = string.strip().split(" ")
    hour = int(parts[0])
    minute = int(parts[1])

    if hour < 12:
        hour += 12

    return f"{hour:02d}:{minute:02d}"


def process_prayer_timings(file: str) -> DataFrame:
    try:
        df = pd.read_csv(file, header=None)

        # Define the column names
        column_names = ["date", "subuh", "syuruk", "zohor", "asar", "maghrib", "isyak"]
        df.columns = column_names

        df["date"] = pd.to_datetime(df["date"], format="%d/%m/%Y")
        df["date"] = df["date"].dt.strftime("%Y-%m-%d")

        df["subuh"] = df["subuh"].apply(process_am_time)
        df["syuruk"] = df["syuruk"].apply(process_am_time)

        df["zohor"] = df["zohor"].apply(process_pm_time)
        df["asar"] = df["asar"].apply(process_pm_time)
        df["maghrib"] = df["maghrib"].apply(process_pm_time)
        df["isyak"] = df["isyak"].apply(process_pm_time)

        return df
    except FileNotFoundError:
        print(f"File {file} was not found.")
        return None
    except Exception as ex:
        print(f"Error occurred: {ex}")
        return None


def convert_to_sql(df: DataFrame) -> str:
    columns = ", ".join([f'"{col}"' for col in df.columns])
    insert_statements = []

    for index, row in df.iterrows():
        values = []
        for col in df.columns:
            val = row[col]
            if pd.isna(val):
                values.append("NULL")
            else:
                values.append(f"'{val}'")

        values_str = ", ".join(values)
        sql = f"\t({values_str})"
        insert_statements.append(sql)

    return f"INSERT INTO prayer_timings ({columns})\nVALUES\n{",\n".join(insert_statements)};"

if __name__ == "__main__":
    df = process_prayer_timings("prayer-timetable-2025.csv")
    sql = convert_to_sql(df)
    try:
        with open("prayer-timetable-2025.sql", "w") as f:
            f.write(sql)
        print("SQL file created successfully.")
    except IOError as ex:
        print(f"Error writing to file: {ex}")

