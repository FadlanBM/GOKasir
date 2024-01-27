package request

type ValidateMemberRequest struct {
	CodeMember string `json:"code"`
	Password   string `json:"password"`
}
