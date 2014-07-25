package cn.ttyhuo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactModel implements Parcelable {

	private String id;
	private String name;
	private String phone;
    private Integer userID;
    private Boolean hasFellow;

	public ContactModel() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeString(id);
		out.writeString(name);
		out.writeString(phone);
        out.writeInt(userID);
        out.writeByte(hasFellow ? (byte) 1 : (byte) 0);
	}

	private ContactModel(Parcel in) {
		id = in.readString();
		name = in.readString();
		phone = in.readString();
        userID = in.readInt();
        hasFellow = (in.readByte() == (byte)1);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<ContactModel> CREATOR = new Parcelable.Creator<ContactModel>() {
		@Override
		public ContactModel createFromParcel(Parcel in) {
			return new ContactModel(in);
		}

		@Override
		public ContactModel[] newArray(int size) {
			return new ContactModel[size];
		}
	};

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public Boolean getHasFellow() {
        return hasFellow;
    }

    public void setHasFellow(Boolean hasFellow) {
        this.hasFellow = hasFellow;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

}
