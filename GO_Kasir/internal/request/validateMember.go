package request

type ValidateMemberRequest struct {
	Name       string `json:"name"`
	CodeMember string `json:"code"`
	Password   string `json:"password"`
}
