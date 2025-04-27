package csu.yulin.model.entity;

import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.DynamicArray;
import org.fisco.bcos.sdk.abi.datatypes.Event;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple5;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 智能合约
 *
 * @author lp
 * @create 2025-03-26
 */
public class CharityDonation extends Contract {
    public static final String[] BINARY_ARRAY = {"608060405234801561001057600080fd5b506000600781905550600060088190555060006009819055506000600a8190555061130e806100406000396000f300608060405260043610610128576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806302da667b1461012d5780630ccc9c30146101645780630cfa2fd8146101e6578063107046bd1461024b578063148bb90a146102a15780632abfab4d146102cc578063312c0612146102f757806336fbad26146103225780633e97b43f1461034d57806386302d84146103b2578063915f67c7146103f757806395a7a80a14610438578063b947192214610495578063ba940fd6146104e0578063c56f3cac14610562578063c655d731146105ad578063d92b4cdf1461060a578063ef07a81f1461068c578063ef88f48b146106e9578063f0f3f2c81461072a578063f8626af814610780578063fd1a71ca146107dd575b600080fd5b34801561013957600080fd5b506101626004803603810190808035906020019092919080359060200190929190505050610828565b005b34801561017057600080fd5b5061018f600480360381019080803590602001909291905050506108eb565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b838110156101d25780820151818401526020810190506101b7565b505050509050019250505060405180910390f35b3480156101f257600080fd5b5061021160048036038101908080359060200190929190505050610956565b6040518086815260200185815260200184815260200183600019166000191681526020018281526020019550505050505060405180910390f35b34801561025757600080fd5b50610276600480360381019080803590602001909291905050506109ee565b6040518085815260200184815260200183815260200182815260200194505050505060405180910390f35b3480156102ad57600080fd5b506102b6610a1e565b6040518082815260200191505060405180910390f35b3480156102d857600080fd5b506102e1610a24565b6040518082815260200191505060405180910390f35b34801561030357600080fd5b5061030c610a2a565b6040518082815260200191505060405180910390f35b34801561032e57600080fd5b50610337610a30565b6040518082815260200191505060405180910390f35b34801561035957600080fd5b5061037860048036038101908080359060200190929190505050610a36565b6040518086815260200185815260200184815260200183600019166000191681526020018281526020019550505050505060405180910390f35b3480156103be57600080fd5b506103f560048036038101908080359060200190929190803590602001909291908035600019169060200190929190505050610a6c565b005b34801561040357600080fd5b50610436600480360381019080803590602001909291908035906020019092919080359060200190929190505050610ba1565b005b34801561044457600080fd5b5061046360048036038101908080359060200190929190505050610d74565b604051808681526020018581526020018481526020018381526020018281526020019550505050505060405180910390f35b3480156104a157600080fd5b506104ca6004803603810190808035906020019092919080359060200190929190505050610daa565b6040518082815260200191505060405180910390f35b3480156104ec57600080fd5b5061050b60048036038101908080359060200190929190505050610dda565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b8381101561054e578082015181840152602081019050610533565b505050509050019250505060405180910390f35b34801561056e57600080fd5b506105976004803603810190808035906020019092919080359060200190929190505050610e45565b6040518082815260200191505060405180910390f35b3480156105b957600080fd5b506105d860048036038101908080359060200190929190505050610e75565b604051808681526020018581526020018481526020018381526020018281526020019550505050505060405180910390f35b34801561061657600080fd5b5061063560048036038101908080359060200190929190505050610f05565b6040518080602001828103825283818151815260200191508051906020019060200280838360005b8381101561067857808201518184015260208101905061065d565b505050509050019250505060405180910390f35b34801561069857600080fd5b506106b760048036038101908080359060200190929190505050610f70565b604051808681526020018581526020018481526020018381526020018281526020019550505050505060405180910390f35b3480156106f557600080fd5b50610728600480360381019080803590602001909291908035906020019092919080359060200190929190505050611000565b005b34801561073657600080fd5b5061075560048036038101908080359060200190929190505050611146565b6040518085815260200184815260200183815260200182815260200194505050505060405180910390f35b34801561078c57600080fd5b506107ab600480360381019080803590602001909291905050506111c0565b604051808681526020018581526020018481526020018381526020018281526020019550505050505060405180910390f35b3480156107e957600080fd5b5061081260048036038101908080359060200190929190803590602001909291905050506111f6565b6040518082815260200191505060405180910390f35b600760008154809291906001019190505550608060405190810160405280600754815260200183815260200182815260200160008152506000806007548152602001908152602001600020600082015181600001556020820151816001015560408201518160020155606082015181600301559050507f45777831981be495bb6dd356533f1d2444bb6f8bee0a64ad7f2055385c6d2921600754838360405180848152602001838152602001828152602001935050505060405180910390a15050565b60606002600083815260200190815260200160002080548060200260200160405190810160405280929190818152602001828054801561094a57602002820191906000526020600020905b815481526020019060010190808311610936575b50505050509050919050565b6000806000806000610966611226565b6003600088815260200190815260200160002060a06040519081016040529081600082015481526020016001820154815260200160028201548152602001600382015460001916600019168152602001600482015481525050905080600001518160200151826040015183606001518460800151955095509550955095505091939590929450565b60006020528060005260406000206000915090508060000154908060010154908060020154908060030154905084565b600a5481565b60085481565b60095481565b60075481565b60036020528060005260406000206000915090508060000154908060010154908060020154908060030154908060040154905085565b60096000815480929190600101919050555060a0604051908101604052806009548152602001848152602001838152602001826000191681526020014281525060036000600954815260200190815260200160002060008201518160000155602082015181600101556040820151816002015560608201518160030190600019169055608082015181600401559050506004600084815260200190815260200160002060095490806001815401808255809150509060018203906000526020600020016000909192909190915055507f95095475e5faf0238ac9f35270b2f1e8031198478263783bcbab998d4a0fc604600954848484426040518086815260200185815260200184815260200183600019166000191681526020018281526020019550505050505060405180910390a1505050565b806000808581526020019081526020016000206003015410151515610c2e576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601a8152602001807f496e73756666696369656e742072616973656420616d6f756e7400000000000081525060200191505060405180910390fd5b600a6000815480929190600101919050555060a060405190810160405280600a5481526020018481526020018381526020018281526020014281525060056000600a5481526020019081526020016000206000820151816000015560208201518160010155604082015181600201556060820151816003015560808201518160040155905050806000808581526020019081526020016000206003016000828254039250508190555060066000848152602001908152602001600020600a5490806001815401808255809150509060018203906000526020600020016000909192909190915055507fcd3344490f61b47a57cee3e8d3f825134b918aab92f0407dff780b101d9bdabf600a5484848442604051808681526020018581526020018481526020018381526020018281526020019550505050505060405180910390a1505050565b60056020528060005260406000206000915090508060000154908060010154908060020154908060030154908060040154905085565b600660205281600052604060002081815481101515610dc557fe5b90600052602060002001600091509150505481565b606060046000838152602001908152602001600020805480602002602001604051908101604052809291908181526020018280548015610e3957602002820191906000526020600020905b815481526020019060010190808311610e25575b50505050509050919050565b600260205281600052604060002081815481101515610e6057fe5b90600052602060002001600091509150505481565b6000806000806000610e85611259565b6005600088815260200190815260200160002060a0604051908101604052908160008201548152602001600182015481526020016002820154815260200160038201548152602001600482015481525050905080600001518160200151826040015183606001518460800151955095509550955095505091939590929450565b606060066000838152602001908152602001600020805480602002602001604051908101604052809291908181526020018280548015610f6457602002820191906000526020600020905b815481526020019060010190808311610f50575b50505050509050919050565b6000806000806000610f80611289565b6001600088815260200190815260200160002060a0604051908101604052908160008201548152602001600182015481526020016002820154815260200160", "038201548152602001600482015481525050905080600001518160200151826040015183606001518460800151955095509550955095505091939590929450565b60086000815480929190600101919050555060a060405190810160405280600854815260200184815260200183815260200182815260200142815250600160006008548152602001908152602001600020600082015181600001556020820151816001015560408201518160020155606082015181600301556080820151816004015590505080600080848152602001908152602001600020600301600082825401925050819055506002600083815260200190815260200160002060085490806001815401808255809150509060018203906000526020600020016000909192909190915055507f5b197864b1e0d4d65361bbf5b9b57c4e7d5c9372e5bc2371c513667c3a97afce60085484848442604051808681526020018581526020018481526020018381526020018281526020019550505050505060405180910390a1505050565b6000806000806111546112b9565b60008087815260200190815260200160002060806040519081016040529081600082015481526020016001820154815260200160028201548152602001600382015481525050905080600001518160200151826040015183606001519450945094509450509193509193565b60016020528060005260406000206000915090508060000154908060010154908060020154908060030154908060040154905085565b60046020528160005260406000208181548110151561121157fe5b90600052602060002001600091509150505481565b60a06040519081016040528060008152602001600081526020016000815260200160008019168152602001600081525090565b60a06040519081016040528060008152602001600081526020016000815260200160008152602001600081525090565b60a06040519081016040528060008152602001600081526020016000815260200160008152602001600081525090565b6080604051908101604052806000815260200160008152602001600081526020016000815250905600a165627a7a723058200ed0c874efe3930de2a25cf794c12864e31a46df26ddb6008769f7c7d5288e940029"};

    public static final String BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"constant\":false,\"inputs\":[{\"name\":\"orgId\",\"type\":\"uint256\"},{\"name\":\"targetAmount\",\"type\":\"uint256\"}],\"name\":\"createProject\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"projectId\",\"type\":\"uint256\"}],\"name\":\"getProjectDonations\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"voucherId\",\"type\":\"uint256\"}],\"name\":\"getVoucher\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"bytes32\"},{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"projects\",\"outputs\":[{\"name\":\"projectId\",\"type\":\"uint256\"},{\"name\":\"orgId\",\"type\":\"uint256\"},{\"name\":\"targetAmount\",\"type\":\"uint256\"},{\"name\":\"raisedAmount\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"fundFlowCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"donationCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"voucherCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"projectCount\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"vouchers\",\"outputs\":[{\"name\":\"voucherId\",\"type\":\"uint256\"},{\"name\":\"projectId\",\"type\":\"uint256\"},{\"name\":\"orgId\",\"type\":\"uint256\"},{\"name\":\"ipfsHash\",\"type\":\"bytes32\"},{\"name\":\"timestamp\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"projectId\",\"type\":\"uint256\"},{\"name\":\"orgId\",\"type\":\"uint256\"},{\"name\":\"ipfsHash\",\"type\":\"bytes32\"}],\"name\":\"uploadVoucher\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"projectId\",\"type\":\"uint256\"},{\"name\":\"recipientId\",\"type\":\"uint256\"},{\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"recordFundFlow\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"fundFlows\",\"outputs\":[{\"name\":\"flowId\",\"type\":\"uint256\"},{\"name\":\"projectId\",\"type\":\"uint256\"},{\"name\":\"recipientId\",\"type\":\"uint256\"},{\"name\":\"amount\",\"type\":\"uint256\"},{\"name\":\"timestamp\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"projectFundFlows\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"projectId\",\"type\":\"uint256\"}],\"name\":\"getProjectVouchers\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"projectDonations\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"flowId\",\"type\":\"uint256\"}],\"name\":\"getFundFlow\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"projectId\",\"type\":\"uint256\"}],\"name\":\"getProjectFundFlows\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"donationId\",\"type\":\"uint256\"}],\"name\":\"getDonation\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"userId\",\"type\":\"uint256\"},{\"name\":\"projectId\",\"type\":\"uint256\"},{\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"donate\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"projectId\",\"type\":\"uint256\"}],\"name\":\"getProject\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"donations\",\"outputs\":[{\"name\":\"donationId\",\"type\":\"uint256\"},{\"name\":\"userId\",\"type\":\"uint256\"},{\"name\":\"projectId\",\"type\":\"uint256\"},{\"name\":\"amount\",\"type\":\"uint256\"},{\"name\":\"timestamp\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"projectVouchers\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"projectId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"orgId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"targetAmount\",\"type\":\"uint256\"}],\"name\":\"ProjectCreated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"donationId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"userId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"projectId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"amount\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"timestamp\",\"type\":\"uint256\"}],\"name\":\"Donated\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"voucherId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"projectId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"orgId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"ipfsHash\",\"type\":\"bytes32\"},{\"indexed\":false,\"name\":\"timestamp\",\"type\":\"uint256\"}],\"name\":\"VoucherUploaded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"flowId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"projectId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"recipientId\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"amount\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"timestamp\",\"type\":\"uint256\"}],\"name\":\"FundFlowRecorded\",\"type\":\"event\"}]"};

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_CREATEPROJECT = "createProject";

    public static final String FUNC_GETPROJECTDONATIONS = "getProjectDonations";

    public static final String FUNC_GETVOUCHER = "getVoucher";

    public static final String FUNC_PROJECTS = "projects";

    public static final String FUNC_FUNDFLOWCOUNT = "fundFlowCount";

    public static final String FUNC_DONATIONCOUNT = "donationCount";

    public static final String FUNC_VOUCHERCOUNT = "voucherCount";

    public static final String FUNC_PROJECTCOUNT = "projectCount";

    public static final String FUNC_VOUCHERS = "vouchers";

    public static final String FUNC_UPLOADVOUCHER = "uploadVoucher";

    public static final String FUNC_RECORDFUNDFLOW = "recordFundFlow";

    public static final String FUNC_FUNDFLOWS = "fundFlows";

    public static final String FUNC_PROJECTFUNDFLOWS = "projectFundFlows";

    public static final String FUNC_GETPROJECTVOUCHERS = "getProjectVouchers";

    public static final String FUNC_PROJECTDONATIONS = "projectDonations";

    public static final String FUNC_GETFUNDFLOW = "getFundFlow";

    public static final String FUNC_GETPROJECTFUNDFLOWS = "getProjectFundFlows";

    public static final String FUNC_GETDONATION = "getDonation";

    public static final String FUNC_DONATE = "donate";

    public static final String FUNC_GETPROJECT = "getProject";

    public static final String FUNC_DONATIONS = "donations";

    public static final String FUNC_PROJECTVOUCHERS = "projectVouchers";

    public static final Event PROJECTCREATED_EVENT = new Event("ProjectCreated",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));

    public static final Event DONATED_EVENT = new Event("Donated",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));

    public static final Event VOUCHERUPLOADED_EVENT = new Event("VoucherUploaded",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Bytes32>() {
            }, new TypeReference<Uint256>() {
            }));

    public static final Event FUNDFLOWRECORDED_EVENT = new Event("FundFlowRecorded",
            Arrays.asList(new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<Uint256>() {
            }));

    protected CharityDonation(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static CharityDonation load(String contractAddress, Client client, CryptoKeyPair credential) {
        return new CharityDonation(contractAddress, client, credential);
    }

    public static CharityDonation deploy(Client client, CryptoKeyPair credential) throws ContractException {
        return deploy(CharityDonation.class, client, credential, getBinary(client.getCryptoSuite()), "");
    }

    public TransactionReceipt createProject(BigInteger orgId, BigInteger targetAmount) {
        final Function function = new Function(
                FUNC_CREATEPROJECT,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(orgId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(targetAmount)),
                Collections.emptyList());
        return executeTransaction(function);
    }

    public byte[] createProject(BigInteger orgId, BigInteger targetAmount, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_CREATEPROJECT,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(orgId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(targetAmount)),
                Collections.emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateProject(BigInteger orgId, BigInteger targetAmount) {
        final Function function = new Function(
                FUNC_CREATEPROJECT,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(orgId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(targetAmount)),
                Collections.emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<BigInteger, BigInteger> getCreateProjectInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_CREATEPROJECT,
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<BigInteger, BigInteger>(

                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue()
        );
    }

    public List getProjectDonations(BigInteger projectId) throws ContractException {
        final Function function = new Function(FUNC_GETPROJECTDONATIONS,
                Collections.singletonList(new Uint256(projectId)),
                Collections.singletonList(new TypeReference<DynamicArray<Uint256>>() {
                }));
        List<Type> result = (List<Type>) executeCallWithSingleValueReturn(function, List.class);
        return convertToNative(result);
    }

    public Tuple5<BigInteger, BigInteger, BigInteger, byte[], BigInteger> getVoucher(BigInteger voucherId) throws ContractException {
        final Function function = new Function(FUNC_GETVOUCHER,
                Collections.singletonList(new Uint256(voucherId)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Bytes32>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<BigInteger, BigInteger, BigInteger, byte[], BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (byte[]) results.get(3).getValue(),
                (BigInteger) results.get(4).getValue());
    }

    public Tuple4<BigInteger, BigInteger, BigInteger, BigInteger> projects(BigInteger param0) throws ContractException {
        final Function function = new Function(FUNC_PROJECTS,
                Collections.singletonList(new Uint256(param0)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue());
    }

    public BigInteger fundFlowCount() throws ContractException {
        final Function function = new Function(FUNC_FUNDFLOWCOUNT,
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public BigInteger donationCount() throws ContractException {
        final Function function = new Function(FUNC_DONATIONCOUNT,
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public BigInteger voucherCount() throws ContractException {
        final Function function = new Function(FUNC_VOUCHERCOUNT,
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public BigInteger projectCount() throws ContractException {
        final Function function = new Function(FUNC_PROJECTCOUNT,
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Tuple5<BigInteger, BigInteger, BigInteger, byte[], BigInteger> vouchers(BigInteger param0) throws ContractException {
        final Function function = new Function(FUNC_VOUCHERS,
                Collections.singletonList(new Uint256(param0)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Bytes32>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<BigInteger, BigInteger, BigInteger, byte[], BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (byte[]) results.get(3).getValue(),
                (BigInteger) results.get(4).getValue());
    }

    public TransactionReceipt uploadVoucher(BigInteger projectId, BigInteger orgId, byte[] ipfsHash) {
        final Function function = new Function(
                FUNC_UPLOADVOUCHER,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(projectId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(orgId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32(ipfsHash)),
                Collections.emptyList());
        return executeTransaction(function);
    }

    public byte[] uploadVoucher(BigInteger projectId, BigInteger orgId, byte[] ipfsHash, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_UPLOADVOUCHER,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(projectId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(orgId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32(ipfsHash)),
                Collections.emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUploadVoucher(BigInteger projectId, BigInteger orgId, byte[] ipfsHash) {
        final Function function = new Function(
                FUNC_UPLOADVOUCHER,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(projectId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(orgId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes32(ipfsHash)),
                Collections.emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<BigInteger, BigInteger, byte[]> getUploadVoucherInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_UPLOADVOUCHER,
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Bytes32>() {
                }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<BigInteger, BigInteger, byte[]>(

                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (byte[]) results.get(2).getValue()
        );
    }

    public TransactionReceipt recordFundFlow(BigInteger projectId, BigInteger recipientId, BigInteger amount) {
        final Function function = new Function(
                FUNC_RECORDFUNDFLOW,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(projectId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(recipientId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(amount)),
                Collections.emptyList());
        return executeTransaction(function);
    }

    public byte[] recordFundFlow(BigInteger projectId, BigInteger recipientId, BigInteger amount, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_RECORDFUNDFLOW,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(projectId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(recipientId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(amount)),
                Collections.emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRecordFundFlow(BigInteger projectId, BigInteger recipientId, BigInteger amount) {
        final Function function = new Function(
                FUNC_RECORDFUNDFLOW,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(projectId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(recipientId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(amount)),
                Collections.emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<BigInteger, BigInteger, BigInteger> getRecordFundFlowInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_RECORDFUNDFLOW,
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<BigInteger, BigInteger, BigInteger>(

                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue()
        );
    }

    public Tuple5<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> fundFlows(BigInteger param0) throws ContractException {
        final Function function = new Function(FUNC_FUNDFLOWS,
                Collections.singletonList(new Uint256(param0)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue(),
                (BigInteger) results.get(4).getValue());
    }

    public BigInteger projectFundFlows(BigInteger param0, BigInteger param1) throws ContractException {
        final Function function = new Function(FUNC_PROJECTFUNDFLOWS,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(param0),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(param1)),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public List getProjectVouchers(BigInteger projectId) throws ContractException {
        final Function function = new Function(FUNC_GETPROJECTVOUCHERS,
                Collections.singletonList(new Uint256(projectId)),
                Collections.singletonList(new TypeReference<DynamicArray<Uint256>>() {
                }));
        List<Type> result = (List<Type>) executeCallWithSingleValueReturn(function, List.class);
        return convertToNative(result);
    }

    public BigInteger projectDonations(BigInteger param0, BigInteger param1) throws ContractException {
        final Function function = new Function(FUNC_PROJECTDONATIONS,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(param0),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(param1)),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Tuple5<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> getFundFlow(BigInteger flowId) throws ContractException {
        final Function function = new Function(FUNC_GETFUNDFLOW,
                Collections.singletonList(new Uint256(flowId)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue(),
                (BigInteger) results.get(4).getValue());
    }

    public List getProjectFundFlows(BigInteger projectId) throws ContractException {
        final Function function = new Function(FUNC_GETPROJECTFUNDFLOWS,
                Collections.singletonList(new Uint256(projectId)),
                Collections.singletonList(new TypeReference<DynamicArray<Uint256>>() {
                }));
        List<Type> result = (List<Type>) executeCallWithSingleValueReturn(function, List.class);
        return convertToNative(result);
    }

    public Tuple5<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> getDonation(BigInteger donationId) throws ContractException {
        final Function function = new Function(FUNC_GETDONATION,
                Collections.singletonList(new Uint256(donationId)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue(),
                (BigInteger) results.get(4).getValue());
    }

    public TransactionReceipt donate(BigInteger userId, BigInteger projectId, BigInteger amount) {
        final Function function = new Function(
                FUNC_DONATE,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(userId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(projectId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(amount)),
                Collections.emptyList());
        return executeTransaction(function);
    }

    public byte[] donate(BigInteger userId, BigInteger projectId, BigInteger amount, TransactionCallback callback) {
        final Function function = new Function(
                FUNC_DONATE,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(userId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(projectId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(amount)),
                Collections.emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForDonate(BigInteger userId, BigInteger projectId, BigInteger amount) {
        final Function function = new Function(
                FUNC_DONATE,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(userId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(projectId),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(amount)),
                Collections.emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<BigInteger, BigInteger, BigInteger> getDonateInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_DONATE,
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<BigInteger, BigInteger, BigInteger>(

                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue()
        );
    }

    public Tuple4<BigInteger, BigInteger, BigInteger, BigInteger> getProject(BigInteger projectId) throws ContractException {
        final Function function = new Function(FUNC_GETPROJECT,
                Collections.singletonList(new Uint256(projectId)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue());
    }

    public Tuple5<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> donations(BigInteger param0) throws ContractException {
        final Function function = new Function(FUNC_DONATIONS,
                Collections.singletonList(new Uint256(param0)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple5<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                (BigInteger) results.get(0).getValue(),
                (BigInteger) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (BigInteger) results.get(3).getValue(),
                (BigInteger) results.get(4).getValue());
    }

    public BigInteger projectVouchers(BigInteger param0, BigInteger param1) throws ContractException {
        final Function function = new Function(FUNC_PROJECTVOUCHERS,
                Arrays.asList(new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(param0),
                        new org.fisco.bcos.sdk.abi.datatypes.generated.Uint256(param1)),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public List<ProjectCreatedEventResponse> getProjectCreatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PROJECTCREATED_EVENT, transactionReceipt);
        ArrayList<ProjectCreatedEventResponse> responses = new ArrayList<ProjectCreatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ProjectCreatedEventResponse typedResponse = new ProjectCreatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.projectId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.orgId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.targetAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeProjectCreatedEvent(String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(PROJECTCREATED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, fromBlock, toBlock, otherTopics, callback);
    }

    public void subscribeProjectCreatedEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(PROJECTCREATED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, callback);
    }

    public List<DonatedEventResponse> getDonatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DONATED_EVENT, transactionReceipt);
        ArrayList<DonatedEventResponse> responses = new ArrayList<DonatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DonatedEventResponse typedResponse = new DonatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.donationId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.userId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.projectId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeDonatedEvent(String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(DONATED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, fromBlock, toBlock, otherTopics, callback);
    }

    public void subscribeDonatedEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(DONATED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, callback);
    }

    public List<VoucherUploadedEventResponse> getVoucherUploadedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(VOUCHERUPLOADED_EVENT, transactionReceipt);
        ArrayList<VoucherUploadedEventResponse> responses = new ArrayList<VoucherUploadedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            VoucherUploadedEventResponse typedResponse = new VoucherUploadedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.voucherId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.projectId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.orgId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.ipfsHash = (byte[]) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeVoucherUploadedEvent(String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(VOUCHERUPLOADED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, fromBlock, toBlock, otherTopics, callback);
    }

    public void subscribeVoucherUploadedEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(VOUCHERUPLOADED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, callback);
    }

    public List<FundFlowRecordedEventResponse> getFundFlowRecordedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(FUNDFLOWRECORDED_EVENT, transactionReceipt);
        ArrayList<FundFlowRecordedEventResponse> responses = new ArrayList<FundFlowRecordedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            FundFlowRecordedEventResponse typedResponse = new FundFlowRecordedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.flowId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.projectId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.recipientId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeFundFlowRecordedEvent(String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(FUNDFLOWRECORDED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, fromBlock, toBlock, otherTopics, callback);
    }

    public void subscribeFundFlowRecordedEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(FUNDFLOWRECORDED_EVENT);
        subscribeEvent(ABI, BINARY, topic0, callback);
    }

    public static class ProjectCreatedEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger projectId;

        public BigInteger orgId;

        public BigInteger targetAmount;
    }

    public static class DonatedEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger donationId;

        public BigInteger userId;

        public BigInteger projectId;

        public BigInteger amount;

        public BigInteger timestamp;
    }

    public static class VoucherUploadedEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger voucherId;

        public BigInteger projectId;

        public BigInteger orgId;

        public byte[] ipfsHash;

        public BigInteger timestamp;
    }

    public static class FundFlowRecordedEventResponse {
        public TransactionReceipt.Logs log;

        public BigInteger flowId;

        public BigInteger projectId;

        public BigInteger recipientId;

        public BigInteger amount;

        public BigInteger timestamp;
    }
}
